package org.akanework.gramophone

import org.akanework.gramophone.logic.utils.ArtistUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtistUtilsTest {

    @Test
    fun testEmptyAndUnknown() {
        assertTrue(ArtistUtils.splitArtistNames(null).isEmpty())
        assertTrue(ArtistUtils.splitArtistNames("").isEmpty())
        assertTrue(ArtistUtils.splitArtistNames("   ").isEmpty())
        assertTrue(ArtistUtils.splitArtistNames("<unknown>").isEmpty())
        assertTrue(ArtistUtils.splitArtistNames("Unknown").isEmpty())
    }

    @Test
    fun testSingleArtist() {
        assertEquals(listOf("Taylor Swift"), ArtistUtils.splitArtistNames("Taylor Swift"))
        assertEquals("Taylor Swift", ArtistUtils.getFirstArtistName("Taylor Swift"))
    }

    @Test
    fun testAmpersand() {
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A & Artist B")
        )
        assertEquals("Artist A", ArtistUtils.getFirstArtistName("Artist A & Artist B"))
    }

    @Test
    fun testCommasAndSemicolons() {
        assertEquals(
            listOf("Artist A", "Artist B", "Artist C"),
            ArtistUtils.splitArtistNames("Artist A, Artist B, Artist C")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A; Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B", "Artist C"),
            ArtistUtils.splitArtistNames("Artist A, Artist B & Artist C")
        )
    }

    @Test
    fun testSlashes() {
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A / Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A/Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A \\ Artist B")
        )
    }

    @Test
    fun testPreserveACDC() {
        assertEquals(
            listOf("AC/DC"),
            ArtistUtils.splitArtistNames("AC/DC")
        )
        assertEquals(
            listOf("AC/DC", "Axl Rose"),
            ArtistUtils.splitArtistNames("AC/DC feat. Axl Rose")
        )
        assertEquals(
            listOf("Artist A", "AC/DC"),
            ArtistUtils.splitArtistNames("Artist A & AC/DC")
        )
    }

    @Test
    fun testFeatAndFeaturing() {
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A feat. Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A feat Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A ft. Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A ft Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A featuring Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A (feat. Artist B)")
        )
        assertEquals(
            listOf("Artist A", "Artist B", "Artist C"),
            ArtistUtils.splitArtistNames("Artist A [feat. Artist B & Artist C]")
        )
    }

    @Test
    fun testOtherConjunctions() {
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A with Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A w/ Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A vs. Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A vs Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A and Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A x Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A X Artist B")
        )
        assertEquals(
            listOf("The xx"),
            ArtistUtils.splitArtistNames("The xx")
        )
    }

    @Test
    fun testPunctuationSeparators() {
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A | Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A + Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A • Artist B")
        )
        assertEquals(
            listOf("Artist A", "Artist B"),
            ArtistUtils.splitArtistNames("Artist A · Artist B")
        )
    }

    @Test
    fun testFullWidthSeparators() {
        assertEquals(
            listOf("Artist A", "Artist B", "Artist C", "Artist D", "Artist E"),
            ArtistUtils.splitArtistNames("Artist A、Artist B，Artist C；Artist D＆Artist E")
        )
    }
}
