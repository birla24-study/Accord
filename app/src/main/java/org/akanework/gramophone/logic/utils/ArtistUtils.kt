/*
 *     Copyright (C) 2024 Akane Foundation
 *
 *     Gramophone is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Gramophone is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.akanework.gramophone.logic.utils

import androidx.media3.common.MediaItem
import org.akanework.gramophone.R

object ArtistUtils {

    private const val ACDC_PLACEHOLDER = "\u0000ACDC\u0000"

    /**
     * Regex matching common artist separators:
     * - Parenthesis/bracket prefixed collaboration: (feat., [ft., etc.
     * - Text collaborations: feat., feat, ft., ft, featuring, with, w/, vs., vs, v., and
     * - Single letter collaboration 'x' / 'X' surrounded by whitespace
     * - Punctuation separators: & / \ , ; | + • · ・ and fullwidth equivalents (＆ ， 、 ； ／)
     */
    private val SEPARATOR_REGEX = Regex(
        """(?i)(?:""" +
            """\s*[\(\[]\s*(?:feat\.?|ft\.?|featuring)\s+""" +
            """|\s+(?:feat\.?|ft\.?|featuring)\s+""" +
            """|\s+(?:with|w\/|vs\.?|v\.?|and)\s+""" +
            """|\s+[xX]\s+""" +
            """|\s*[/\\;,|•·・+&，、；＆／]+\s*""" +
        """)"""
    )

    /**
     * Splits a raw artist string into a list of individual artist names.
     */
    fun splitArtistNames(rawArtist: String?): List<String> {
        if (rawArtist.isNullOrBlank()) return emptyList()
        val trimmed = rawArtist.trim()
        if (trimmed == "<unknown>" || trimmed.equals("unknown", ignoreCase = true)) return emptyList()

        // Preserve AC/DC so it's not split on '/'
        val protectedText = trimmed.replace(Regex("""(?i)\bAC/DC\b"""), ACDC_PLACEHOLDER)

        val parts = protectedText.split(SEPARATOR_REGEX)
        val result = mutableListOf<String>()

        for (part in parts) {
            val restored = part.replace(ACDC_PLACEHOLDER, "AC/DC")
            val cleaned = cleanArtistName(restored)
            if (cleaned.isNotBlank() && !result.contains(cleaned)) {
                result.add(cleaned)
            }
        }

        return result
    }

    private fun cleanArtistName(raw: String): String {
        var name = raw.trim()

        // Strip surrounding quotes
        if ((name.startsWith("\"") && name.endsWith("\"")) || (name.startsWith("'") && name.endsWith("'"))) {
            if (name.length >= 2) {
                name = name.substring(1, name.length - 1).trim()
            }
        }

        // Strip surrounding parentheses or brackets
        while ((name.startsWith("(") && name.endsWith(")")) || (name.startsWith("[") && name.endsWith("]"))) {
            if (name.length >= 2) {
                name = name.substring(1, name.length - 1).trim()
            } else break
        }

        // Strip unmatched closing parenthesis/bracket at end
        if (name.endsWith(")") && !name.contains("(")) {
            name = name.substring(0, name.length - 1).trim()
        }
        if (name.endsWith("]") && !name.contains("[")) {
            name = name.substring(0, name.length - 1).trim()
        }

        // Strip unmatched opening parenthesis/bracket at start
        if (name.startsWith("(") && !name.contains(")")) {
            name = name.substring(1).trim()
        }
        if (name.startsWith("[") && !name.contains("]")) {
            name = name.substring(1).trim()
        }

        return name
    }

    /**
     * Returns the first/primary artist name from a raw artist string.
     */
    fun getFirstArtistName(rawArtist: String?): String? {
        return splitArtistNames(rawArtist).firstOrNull()
    }

    /**
     * Resolves the target artist position and item type (R.id.artist or R.id.album_artist)
     * for a given raw artist string, targeting the first artist.
     */
    fun findArtistPosition(
        artistList: List<MediaStoreUtils.Artist>?,
        albumArtistList: List<MediaStoreUtils.Artist>?,
        rawArtist: String?,
        mediaItem: MediaItem? = null
    ): Pair<Int, Int>? {
        if (rawArtist.isNullOrBlank()) return null
        val firstArtist = getFirstArtistName(rawArtist) ?: rawArtist

        // Search artistList first
        var pos = artistList?.indexOfFirst { a ->
            a.title == firstArtist && (mediaItem == null || a.songList.any { s -> s.mediaId == mediaItem.mediaId } || a.songList.contains(mediaItem))
        }?.takeIf { it != -1 } ?: artistList?.indexOfFirst { a -> a.title == firstArtist }?.takeIf { it != -1 }

        if (pos != null) {
            return Pair(pos, R.id.artist)
        }

        // Fallback to albumArtistList
        pos = albumArtistList?.indexOfFirst { a ->
            a.title == firstArtist && (mediaItem == null || a.songList.any { s -> s.mediaId == mediaItem.mediaId } || a.songList.contains(mediaItem))
        }?.takeIf { it != -1 } ?: albumArtistList?.indexOfFirst { a -> a.title == firstArtist }?.takeIf { it != -1 }

        if (pos != null) {
            return Pair(pos, R.id.album_artist)
        }

        return null
    }
}
