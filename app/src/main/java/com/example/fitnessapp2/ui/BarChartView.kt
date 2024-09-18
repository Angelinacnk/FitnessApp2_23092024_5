package com.example.fitnessapp2.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.TextView
import com.example.fitnessapp2.R

class BarChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var data: List<Float> = listOf()
    private var labels: List<String> = listOf()

    // Zeichnen der Balken
    private val barPaint = Paint().apply {
        color = Color.parseColor("#b1b1b1")
        style = Paint.Style.FILL
    }

    // Zeichnen des Texts auf den Balken
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    // Zeichnen der Wochentage unterhalb der Balken
    private val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private var popupWindow: PopupWindow? = null

    /**
     * Setzt die Daten und Labels für das Diagramm und fordert eine Neuzeichnung an
     * @param data Liste der Schrittzahlen
     * @param labels Liste der Wochentage
     */
    fun setData(data: List<Float>, labels: List<String>) {
        this.data = data
        this.labels = labels
        invalidate() // Anforderung Neuzeichnung
    }

    /**
     * Zeichnet das Diagramm
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val barWidth = (width / (data.size * 2.5)).toFloat()
        val maxValue = data.maxOrNull() ?: 1f
        val scaleFactor = (height * 0.5) / maxValue

        val offset = (width - (data.size * barWidth * 2.5 - barWidth)) / 2

        data.forEachIndexed { index, value ->
            val left = (index * 2.5 * barWidth).toFloat() + offset
            val top = height - (value * scaleFactor) - 60
            val right = left + barWidth
            val bottom = height - 40f

            // Zeichnen des Balken
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, barPaint)

            // Berechnung Mitte des Balkens für die Schrittanzahl
            val stepText = String.format("%,.0f", value).replace(',', '.')
            val textY = (top + bottom) / 2 // Mitte des Balkens
            canvas.drawText(stepText, ((left + barWidth / 2).toFloat()), textY.toFloat(), textPaint)

            // Berechnung  Position Der Wochentage unterhalb des Balkens
            val labelY = bottom + 30f // Abstand vom unteren Rand des Balkens
            canvas.drawText(labels[index], ((left + barWidth / 2).toFloat()), labelY, labelPaint)
        }
    }

    /**
     * Anzeige Popup mit weiteren Informationen bei Berührung des Balken
     * @param event MotionEvent
     * @return true, wenn  Ereignis verarbeitet wurde
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            val barWidth = (width / (data.size * 2.5)).toFloat()
            val offset = (width - (data.size * barWidth * 2.5 - barWidth)) / 2

            data.forEachIndexed { index, value ->
                val left = (index * 2.5 * barWidth).toFloat() + offset
                val right = left + barWidth

                if (x in left..right) {
                    // Berechnung Kilometer und Kalorien für den berührten Balken
                    val kilometers = value * 0.0008f
                    val calories = value * 0.05f

                    // Anzeige Popup mit Informationen
                    showPopup(kilometers, calories, event.rawX, event.rawY)
                    return true
                }
            }
        }

        if (popupWindow != null && event.action == MotionEvent.ACTION_DOWN) {
            // Schließen des Popups beim Klicken auf Hintergrund
            popupWindow?.dismiss()
            popupWindow = null
        }

        return super.onTouchEvent(event)
    }

    /**
     * Zeigt  Popup mit zusätzlichen Informationen an
     * @param kilometers Berechnete Kilometer
     * @param calories berechnete Kalorien
     * @param x X-Position des Popups
     * @param y Y-Position des Popups
     */
    private fun showPopup(kilometers: Float, calories: Float, x: Float, y: Float) {
        popupWindow?.dismiss()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.popup_info, null)
        val popupTextView = view.findViewById<TextView>(R.id.popupText)

        // Text im Popup
        val text = "Kilometer: %.2f km\nKalorien: %.2f kcal".format(kilometers, calories).replace('.', ',')
        popupTextView.text = text

        // Erstellen des Popups
        popupWindow = PopupWindow(view, 600, 400, true)
        popupWindow?.showAtLocation(this, 0, x.toInt(), y.toInt())
    }
}