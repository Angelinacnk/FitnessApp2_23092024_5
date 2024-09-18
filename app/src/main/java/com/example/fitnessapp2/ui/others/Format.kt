package com.example.fitnessapp2.utils

import java.text.SimpleDateFormat
import java.util.*

object Format {

    /**
     * Formatiert die Anzahl der Schritte
     * @param steps  Anzahl der Schritte.
     * @return  formatierte Schrittanzahl
     */
    fun formatSteps(steps: Float): String {
        return "%,d".format(steps.toInt())
    }

    /**
     * Wandelt die Anzahl der Schritte in Kilometer um
     * @param steps Anzahl der Schritte
     * @return Anzahl der Kilometer
     */
    fun stepsToKilometers(steps: Float): Float {
        return steps * 0.0008f // 1 Schritt = 0,8 Meter
    }

    /**
     * Wandelt die Anzahl der Schritte in Kalorien um
     * @param steps Anzahl der Schritte.
     * @return Anzahl der Kalorien.
     */
    fun stepsToCalories(steps: Float): Float {
        return steps * 0.05f // 1 Schritt = 0,05 Kalorien
    }

    /**
     * Gibt den Wochentag für ein bestimmtes Datum zurück
     * @param dateString  Datum im Format "yyyy-MM-dd"
     * @return  Wochentag des Datums
     */
    fun getDayOfWeek(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayOfWeekFormat.format(date ?: Date())
    }
}