package com.example.fitnessapp2.utils

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StepsPreferences {

    private const val PREFS_NAME = "steps_prefs"

    /**
     * Gibt die SharedPreferences für die Anwendung zurück
     * @param context Kontext der Anwendung
     * @return  SharedPreferences-Instanz
     */
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Laden der Schritte der letzten 7 Tage aus den SharedPreferences
     * @param context  Kontext der Anwendung
     * @return Map, die die Schritte für die letzten 7 Tage speichert
     */
    fun getStepsForLast7Days(context: Context): Map<String, Float> {
        val sharedPreferences = getSharedPreferences(context)
        val stepsMap = mutableMapOf<String, Float>()

        // Schleife zum Laden der Schritte für die letzten 7 Tage
        for (i in 0 until 7) {
            val date = getDateDaysAgo(i)
            stepsMap[date] = sharedPreferences.getFloat(date, 0f)
        }
        return stepsMap
    }

    /**
     * Speichert die Schritte für den heutigen Tag in den SharedPreferences
     * @param context  Kontext der Anwendung
     * @param steps  Anzahl der Schritte
     */
    fun saveStepsForToday(context: Context, steps: Float) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val todayDate = getDateDaysAgo(0)
        editor.putFloat(todayDate, steps)
        editor.apply() // Speichern der Änderungen
    }

    /**
     * Gibt das Datum der vergangenen Tage im Format "yyyy-MM-dd" zurück
     * @param daysAgo  Anzahl der Tage, die zurückgerechnet werden sollen
     * @return  Datum im Format "yyyy-MM-dd"
     */
    private fun getDateDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}