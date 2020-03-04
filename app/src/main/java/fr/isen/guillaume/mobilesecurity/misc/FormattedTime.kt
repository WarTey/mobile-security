package fr.isen.guillaume.mobilesecurity.misc

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class FormattedTime {
    companion object {
        fun hoursMinutes(millis: Long?): String {
            return SimpleDateFormat("HH:mm", Locale.FRENCH).format(millis)
        }

        fun dayMonth(millis: Long?): String {
            return SimpleDateFormat("dd/MM", Locale.FRENCH).format(millis)
        }

        fun year(millis: Long?): String {
            return SimpleDateFormat("yyyy", Locale.FRENCH).format(millis)
        }
    }
}