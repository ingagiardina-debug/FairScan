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
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
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
    val tessdataDir: File,
) {

    companion object {
        private val ENABLED_LANGUAGES = stringSetPreferencesKey("enabled_ocr_languages")
    }

    val enabledLanguages: Flow<Set<String>> =
        dataStore.data.map { prefs ->
            prefs[ENABLED_LANGUAGES] ?: emptySet()
        }

    suspend fun getInstalledLanguages(): Set<String> =
        withContext(Dispatchers.IO) {
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

    suspend fun downloadLanguage(code: String, onProgress: (DownloadProgress) -> Unit) =
        withContext(Dispatchers.IO) {
            require(code in AVAILABLE_LANGUAGE_CODES)
            val expected = OCR_FILE_HASHES[code]
                ?: throw IOException("No pinned hash for OCR language $code")
            tessdataDir.mkdirs()

            // Pinned to an exact commit so the content cannot change under us.
            val url = URL("https://raw.githubusercontent.com/tesseract-ocr/tessdata_fast/$TESSDATA_COMMIT/$code.traineddata")
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
            } finally {
                connection.disconnect()
                tempFile.delete()
            }
        }

    suspend fun deleteLanguage(code: String) {
        val file = File(tessdataDir, "$code.traineddata")
        file.delete()
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
