package personal.ivan.silkrode.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateFormatUtil {

    /**
     * Format date string to your expected pattern
     *
     * @param input         origin date string
     * @param inputPattern  date pattern of origin date string
     * @param outputPattern expected output pattern
     * @param outputLocale  locale of expected output pattern
     */
    fun formatDateString(
        input: String,
        inputPattern: String,
        inputLocale: Locale? = Locale.ENGLISH,
        outputPattern: String,
        outputLocale: Locale? = Locale.getDefault()
    ): String {

        // check input params
        if (input.isEmpty()
            || inputPattern.isEmpty()
            || outputPattern.isEmpty()
        ) {
            return ""
        }

        // date of input
        val origin =
            SimpleDateFormat(
                inputPattern,
                inputLocale ?: Locale.ENGLISH
            )

        // date of output
        val output =
            SimpleDateFormat(
                outputPattern,
                outputLocale ?: Locale.getDefault()
            )

        // start format
        return try {
            origin.parse(input)
                ?.let { output.format(it) }
                ?: ""
        } catch (e: ParseException) {
            ""
        }
    }

    /**
     * Format duration to text
     */
    fun formatPlayerDuration(duration: Long) =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
}