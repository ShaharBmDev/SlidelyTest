package il.co.sbm.slidelytask.utils

import android.icu.text.SimpleDateFormat
import java.util.*

object DateTileUtils {

    const val DATE_AND_TIME_PATTERN: String = "dd/MM/yyyy hh:mm:ss"
    const val SECOND_IN_MILLISECONDS: Int = 1000

    /**
     * Converts milliseconds to [dd/MM/yyyy hh:mm:ss][DATE_AND_TIME_PATTERN] pattern string.
     * @param iTimeInMilliseconds the time in milliseconds to display
     * @return A string representing the time in [dd/MM/yyyy hh:mm:ss][DATE_AND_TIME_PATTERN] pattern.
     */
    fun convertMillisecondsToDateTimeFormat(iTimeInMilliseconds: Long) : String{
        val result: String

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = iTimeInMilliseconds

        result = SimpleDateFormat(DATE_AND_TIME_PATTERN).format(calendar.time)

        return result
    }
}