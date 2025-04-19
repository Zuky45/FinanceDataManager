package com.example.datamanager.frontend.main_pages.graph_page

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * Data class representing the UI state for a model.
 *
 * @property data The data frame containing the model's data. Can be null if no data is available.
 * @property isLoading A flag indicating whether the model's data is currently being loaded.
 * @property error A string containing an error message if an error occurred, or null if no error exists.
 * @property columnName The name of the column in the data frame that corresponds to the model's data.
 * @property displayName The display name of the model, used for UI purposes.
 */
data class ModelUIState(
    val data: DataFrame<*>?,
    val isLoading: Boolean,
    val error: String?,
    val columnName: String,
    val displayName: String
)

/**
 * Enum class representing the different types of models available.
 *
 * @property displayName The display name of the model type, used for UI purposes.
 */
enum class ModelType(val displayName: String) {
    /** Represents raw data with no model applied. */
    NONE("Raw Data"),

    /** Represents an approximation model. */
    APPROXIMATION("Approximation"),

    /** Represents a moving average filtration model. */
    MAFILTRATION("Moving Average"),

    /** Represents an auto-regressive prediction model. */
    ARPREDICTION("AR Prediction");

    /**
     * Retrieves the column name associated with the model type.
     *
     * @return The column name as a string. Returns an empty string for the NONE type.
     */
    fun getColumnName(): String = when(this) {
        NONE -> ""
        APPROXIMATION -> "Approximation"
        MAFILTRATION -> "MaFiltration"
        ARPREDICTION -> "Prediction"
    }
}