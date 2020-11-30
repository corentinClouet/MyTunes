package com.coreclouet.mytunes.converter

import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {
    companion object {
        /**
         * Convert milliseconds to mm:ss format
         */
        fun convertTimeInMillisToMediaFormat(timeInMillis: Int): String {
            val fmt = SimpleDateFormat("mm:ss", Locale.FRANCE)
            return fmt.format(timeInMillis)
        }
    }
}