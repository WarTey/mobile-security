package fr.isen.guillaume.mobilesecurity.misc

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

        fun dayMonthYear(millis: Long?): String {
            return SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(millis)
        }

        fun parse(dateStr: String, french: Boolean): Long {
            return Calendar.getInstance().also { cal ->
                dateStr.split('/').map { str -> Integer.parseInt(str) }.let {
                    cal.set(it[2], it[french.compareTo(false)]-1, it[french.compareTo(true)])
                }
            }.timeInMillis
        }
    }
}