package com.example.fitnessapp2.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitnessapp2.R
import com.example.fitnessapp2.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.fitnessapp2.ui.BarChartView
import com.example.fitnessapp2.utils.StepsPreferences

class HomeFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private var popupWindow: PopupWindow? = null

    @SuppressLint("SetTextI18n", "ServiceCast")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialisierung ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Initialisierung Binding-Objekt
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Aktualisierung Schritte heute
        homeViewModel.steps.observe(viewLifecycleOwner) {
            binding.textHome.text = "Schritte heute: ${formatSteps(it)}"
        }

        // Initialisierung des SensorManagers und des Schrittzähler-Sensors
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Registrierung des Sensor-Listener
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Log.d("HomeFragment", "Kein Schritt-Sensor gefunden")
        }

        // Füllen des Diagramms mit Daten zu den Schritten
        setupBarChart()

        // Verschieben des TextView nach oben
        val params = binding.textHome.layoutParams as ConstraintLayout.LayoutParams
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.topMargin = 16 // Abstand in Pixeln
        binding.textHome.layoutParams = params

        return root
    }

    /**
     * Vorbereitung der Daten für BarChart
     */
    private fun setupBarChart() {
        val stepsMap = StepsPreferences.getStepsForLast7Days(requireContext())

        if (stepsMap.isEmpty()) {
            Log.d("HomeFragment", "Keine Schritte-Daten für die letzten 7 Tage")
        }

        // Sortieren der Daten
        val sortedKeys = stepsMap.keys.sorted()
        val sortedValues = sortedKeys.map { stepsMap[it] ?: 0f }
        val labels = sortedKeys.map { getDayOfWeek(it) }

        // Setzen der Daten in ein Diagramm
        binding.barChart.setData(sortedValues, labels)
    }

    /**
     * Konvertiert ein Datum in den Wochentag
     * @param date  Datum im Format "yyyy-MM-dd"
     * @return Wochentag
     */
    private fun getDayOfWeek(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(date) ?: Date()
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.GERMAN)
        return dayOfWeek ?: ""
    }

    /**
     * Formatierung der Schritte für die Anzeige
     * @param steps  Anzahl der Schritte
     * @return formatierte Schrittanzahl
     */
    private fun formatSteps(steps: Float): String {
        return String.format(Locale.GERMANY, "%,.0f", steps)
    }

    /**
     * Wird aufgerufen, wenn der Sensor einen neuen Wert liefert
     * @param event  SensorEvent
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps - previousTotalSteps

            // Speichern der Schritte für den heute
            StepsPreferences.saveStepsForToday(requireContext(), currentSteps)

            // Aktualisieren des ViewModels mit den neuen Daten
            homeViewModel.updateSteps(currentSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        // Registrierung des Sensor-Listeners beim Fortsetzen der Aktivität
        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Entfernen des Sensor-Listeners beim Pausieren der Aktivität
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Setzen des Binding-Objekt auf null, um Speicherlecks zu vermeiden
        _binding = null
    }

    /**
     * Zeigt Popup mit den Kilometer- und Kalorieninformationen an
     * @param kilometers  Kilometeranzahl
     * @param calories  Kalorienanzahl
     * @param x X-Position für Popup
     * @param y Y-Position für Popup
     */
    private fun showPopup(kilometers: Float, calories: Float, x: Float, y: Float) {
        // Falls ein Popup bereits existiert, schließt es
        popupWindow?.dismiss()

        // Laden des Layouts für Popup
        val inflater = LayoutInflater.from(requireContext())
        val popupLayout = inflater.inflate(R.layout.popup_info, null)

        // Setzen des Textes
        val popupTextView = popupLayout.findViewById<TextView>(R.id.popupText)
        val text = "Kilometer: %.2f km\nKalorien: %.2f kcal".format(kilometers, calories)
        popupTextView.text = text

        // Erstellen und Anzeige des Popup-Fenster
        popupWindow = PopupWindow(popupLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAtLocation(requireView(), 0, x.toInt(), y.toInt())

        // Schließen des Popups, wenn der Hintergrund angeklickt wird
        popupLayout.setOnClickListener {
            popupWindow?.dismiss()
        }
    }
}