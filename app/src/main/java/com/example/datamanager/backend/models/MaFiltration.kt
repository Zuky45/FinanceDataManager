package com.example.datamanager.backend.models

import org.jetbrains.kotlinx.dataframe.DataFrame

class MaFiltration : Model("MA Filtration") {
    override fun calculateModel() {
        // Implement the logic to calculate the MA Filtration model
        throw Exception("Not implemented yet")
    }

    override fun getModel(): DataFrame<*>? {
        // Return the calculated DataFrame for the MA Filtration model
        throw Exception("Not implemented yet")
    }
}