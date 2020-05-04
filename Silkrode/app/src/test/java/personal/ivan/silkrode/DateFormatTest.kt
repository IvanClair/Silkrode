package personal.ivan.silkrode

import org.junit.Assert
import org.junit.Test
import personal.ivan.silkrode.io.model.Collection
import personal.ivan.silkrode.io.model.ContentFeed
import personal.ivan.silkrode.util.DateFormatUtil

class DateFormatTest {

    @Test
    fun formatCollectionReleaseDate_isCorrect() {
        val expected = "09 Mar 2020"
        val actual =
            DateFormatUtil().formatDateString(
                input = "2020-03-09T19:54:00Z",
                inputPattern = Collection.DATE_PATTERN,
                outputPattern = "dd MMM yyyy"
            )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun formatContentFeedPublishedDate_isCorrect() {
        val expected = "Mon, 09 Mar 2020"
        val actual =
            DateFormatUtil().formatDateString(
                input = "Mon, 09 Mar 2020 15:01:16 +0000",
                inputPattern = ContentFeed.DATE_PATTERN,
                outputPattern = "EEE, dd MMM yyyy"
            )
        Assert.assertEquals(expected, actual)
    }
}