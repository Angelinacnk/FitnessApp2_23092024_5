package com.example.fitnessapp2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _steps = MutableLiveData<Float>().apply {
        value = 0f
    }
    val steps: LiveData<Float> = _steps

    private val _calories = MutableLiveData<Float>().apply {
        value = 0f
    }
    val calories: LiveData<Float> = _calories

    private val _distance = MutableLiveData<Float>().apply {
        value = 0f
    }
    val distance: LiveData<Float> = _distance

    fun updateSteps(currentSteps: Float) {
        _steps.value = currentSteps
        _calories.value = calculateCalories(currentSteps)
        _distance.value = calculateDistance(currentSteps)
    }

    private fun calculateCalories(steps: Float): Float {
        return steps * 0.04f
    }

    private fun calculateDistance(steps: Float): Float {
        // Schätzung: 1 Schritt = 0.7 Meter (Basiswert aus Internet)
        val meters = steps * 0.7f
        return meters / 1000 // Kilometer
    }
}