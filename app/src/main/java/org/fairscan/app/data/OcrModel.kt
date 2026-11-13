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

/**
 * Which family of Tesseract language files to use.
 * Each model keeps its own downloaded files, so switching back and forth does not
 * require downloading again.
 */
enum class OcrModel(
    /** Directory under the app's files dir; Tesseract expects a "tessdata" child. */
    val dirName: String,
    val repoName: String,
    val commit: String,
    val hashes: Map<String, OcrFileHash>,
) {
    /** Small and quick (tessdata_fast). FairScan's original choice. */
    FAST("tesseract", "tessdata_fast", TESSDATA_COMMIT, OCR_FILE_HASHES),

    /** Larger and slower, but more accurate (tessdata_best). */
    BEST("tesseract_best", "tessdata_best", TESSDATA_BEST_COMMIT, OCR_BEST_FILE_HASHES);

    fun downloadUrl(code: String): String =
        "https://raw.githubusercontent.com/tesseract-ocr/$repoName/$commit/$code.traineddata"

    companion object {
        fun fromName(name: String?): OcrModel =
            entries.firstOrNull { it.name == name } ?: FAST
    }
}
