package com.example.datamanager.backend.models

import org.jetbrains.kotlinx.dataframe.DataFrame

class ArPrediction : Model("RA Prediction") {
    override fun calculateModel() {
        // Implement the logic to calculate the RA Prediction model
        throw Exception("Not implemented yet")
    }

    override fun getModel(): DataFrame<*>? {
        // Return the calculated DataFrame for the RA Prediction model
        throw Exception("Not implemented yet")
    }
}