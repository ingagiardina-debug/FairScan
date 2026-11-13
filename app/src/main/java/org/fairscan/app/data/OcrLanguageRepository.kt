/*
 * Copyright 2025-2026 The FairScan authors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.fairscan.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.fairscan.app.data.OcrLanguage.Companion.AVAILABLE_LANGUAGE_CODES
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class OcrLanguageRepository(
    private val dataStore: DataStore<Preferences>,
    /** The app's files dir; each [OcrModel] stores its files in its own subdirectory. */
    private val filesDir: File,
) {

    companion object {
        private val ENABLED_LANGUAGES = stringSetPreferencesKey("enabled_ocr_languages")
        private val OCR_MODEL = stringPreferencesKey("ocr_model")
    }

    val enabledLanguages: Flow<Set<String>> =
        dataStore.data.map { prefs ->
            prefs[ENABLED_LANGUAGES] ?: emptySet()
        }

    val ocrModel: Flow<OcrModel> =
        dataStore.data.map { prefs -> OcrModel.fromName(prefs[OCR_MODEL]) }.distinctUntilChanged()

    /** Incremented whenever language files are added or removed, so OCR can reload them. */
    private val _filesVersion = MutableStateFlow(0)
    val filesVersion: StateFlow<Int> = _filesVersion

    suspend fun currentModel(): OcrModel = ocrModel.first()

    suspend fun setOcrModel(model: OcrModel) {
        dataStore.edit { prefs -> prefs[OCR_MODEL] = model.name }
    }

    fun tessdataDir(model: OcrModel): File = File(filesDir, "${model.dirName}/tessdata")

    suspend fun getInstalledLanguages(): Set<String> = getInstalledLanguages(currentModel())

    suspend fun getInstalledLanguages(model: OcrModel): Set<String> =
        withContext(Dispatchers.IO) {
            val tessdataDir = tessdataDir(model)
            if (!tessdataDir.exists()) {
                return@withContext emptySet()
            }
            return@withContext tessdataDir
                .listFiles { _, name -> name.endsWith(".traineddata") }
                ?.map { f -> f.nameWithoutExtension }
                ?.filter { it in AVAILABLE_LANGUAGE_CODES }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun getActiveLanguages(): Set<String> {
        val enabled = enabledLanguagesValue()
        val installed = getInstalledLanguages()
        return enabled.intersect(installed)
    }

    suspend fun setLanguageEnabled(
        code: String,
        enabled: Boolean,
    ) {
        require(code in AVAILABLE_LANGUAGE_CODES)
        if (enabled && code !in getInstalledLanguages()) {
            return
        }
        dataStore.edit { prefs ->
            val current = prefs[ENABLED_LANGUAGES]?.toMutableSet() ?: mutableSetOf()
            if (enabled) {
                current.add(code)
            } else {
                current.remove(code)
            }
            prefs[ENABLED_LANGUAGES] = current
        }
    }

    data class DownloadProgress(
        val downloadedBytes: Long,
        val totalBytes: Long?,
    )

    /** Downloads [code] for the currently selected [OcrModel]. */
    suspend fun downloadLanguage(code: String, onProgress: (DownloadProgress) -> Unit) =
        downloadLanguage(code, currentModel(), onProgress)

    suspend fun downloadLanguage(
        code: String,
        model: OcrModel,
        onProgress: (DownloadProgress) -> Unit,
    ) = withContext(Dispatchers.IO) {
        require(code in AVAILABLE_LANGUAGE_CODES)
        val expected = model.hashes[code]
            ?: throw IOException("No pinned hash for OCR language $code (${model.name})")
        val tessdataDir = tessdataDir(model)
        tessdataDir.mkdirs()

        // Pinned to an exact commit so the content cannot change under us.
        val url = URL(model.downloadUrl(code))
        val targetFile = File(tessdataDir, "$code.traineddata")
        val tempFile = File(tessdataDir, "$code.download")

        val connection = (url.openConnection() as HttpURLConnection)
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.instanceFollowRedirects = true
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP ${connection.responseCode}")
            }
            // Git blob hash: SHA-1 over "blob <size>\0" followed by the file content.
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update("blob ${expected.size}\u0000".toByteArray(Charsets.US_ASCII))
            var downloadedBytes = 0L
            connection.inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        coroutineContext.ensureActive()
                        val read = input.read(buffer)
                        if (read < 0) break
                        downloadedBytes += read
                        if (downloadedBytes > expected.size) {
                            throw IOException("Downloaded file larger than expected")
                        }
                        output.write(buffer, 0, read)
                        digest.update(buffer, 0, read)
                        onProgress(DownloadProgress(downloadedBytes, expected.size))
                    }
                }
            }
            if (downloadedBytes != expected.size) {
                throw IOException("Downloaded file has wrong size")
            }
            val actualHash = digest.digest().joinToString("") { "%02x".format(it) }
            if (actualHash != expected.gitBlobSha1) {
                throw IOException("Downloaded file failed integrity check")
            }
            if (!tempFile.renameTo(targetFile)) {
                throw IOException("Failed to move downloaded file")
            }
            _filesVersion.value++
        } finally {
            connection.disconnect()
            tempFile.delete()
        }
    }

    /** Deletes [code] for the currently selected model only. */
    suspend fun deleteLanguage(code: String) {
        val file = File(tessdataDir(currentModel()), "$code.traineddata")
        file.delete()
        _filesVersion.value++
        setLanguageEnabled(code, false)
    }

    suspend fun buildTesseractLanguageString(): String {
        return getActiveLanguages()
            .sorted()
            .joinToString("+")
    }

    private suspend fun enabledLanguagesValue(): Set<String> {
        return enabledLanguages.map { it }.first()
    }
}
