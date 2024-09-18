package com.example.fitnessapp2.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.TextView
import com.example.fitnessapp2.R

/**
 * Hilfsklasse zum Anzeigen vom Popups
 */
object Popup {
    /**
     * Zeigt ein Popup-Fenster mit  Kilometern und Kalorien an
     * @param context  Kontext, in dem das Popup angezeigt wird
     * @param x  X-Position des Popups auf dem Bildschirm
     * @param y  Y-Position des Popups auf dem Bildschirm
     * @param kilometers Zurückgelegte Entfernung in Kilometern
     * @param calories Verbrannten Kalorien
     */
    fun showPopup(context: Context, x: Float, y: Float, kilometers: Float, calories: Float) {
        // Erstellen des Popups
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.popup_info, null)
        val popupTextView = view.findViewById<TextView>(R.id.popupText)

        // Text mit Kilometern und Kalorien
        // Formatierung: 2 Dezimalstellen und Ersetzung des Punktes durch Komma
        val text = "Kilometer: %.2f km\nKalorien: %.2f kcal".format(kilometers, calories).replace('.', ',')
        popupTextView.text = text

        // Erstellen des PopupWindows (Größe)
        val popupWindow = PopupWindow(view, 600, 400, true)

        // Anzeigen des PopupWindows
        popupWindow.showAtLocation((context as android.app.Activity).window.decorView, Gravity.NO_GRAVITY, x.toInt(), y.toInt())
    }
}