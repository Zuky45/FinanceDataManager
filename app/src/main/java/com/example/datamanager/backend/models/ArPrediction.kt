package com.example.datamanager.backend.models

import com.example.datamanager.backend.api_manager.ApiManager
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

/**
 * Represents an Auto-Regressive (AR) prediction model.
 *
 * @property order The number of previous data points used for prediction. Must be >= 1.
 * @property predictionHorizon The number of future data points to predict. Must be >= 1.
 * @constructor Initializes the AR prediction model with an optional DataFrame, order, and prediction horizon.
 */
class ArPrediction(
    dataFrame: DataFrame<*>? = null,
    private var order: Int = 5,
    private var predictionHorizon: Int = 10
) : Model("AR Prediction", dataFrame) {

    private var _predictionModel: DataFrame<*>? = null // Holds the resulting prediction model as a DataFrame.
    private var _coefficients: DoubleArray? = null // Stores the coefficients of the AR model.

    /**
     * Sets the order of the AR model.
     *
     * @param order The number of previous data points to use. Must be positive.
     * @throws IllegalArgumentException if the order is less than 1.
     */
    fun setOrder(order: Int) {
        if (order < 1) throw IllegalArgumentException("Order must be positive")
        this.order = order
    }

    /**
     * Sets the prediction horizon for the AR model.
     *
     * @param horizon The number of future data points to predict. Must be positive.
     * @throws IllegalArgumentException if the prediction horizon is less than 1.
     */
    fun setPredictionHorizon(horizon: Int) {
        if (horizon < 1) throw IllegalArgumentException("Prediction horizon must be positive")
        this.predictionHorizon = horizon
    }

    /**
     * Calculates the AR prediction model based on the input DataFrame.
     *
     * - Extracts time and value columns from the DataFrame.
     * - Fits an AR model to the data using the specified order.
     * - Generates future predictions based on the fitted model.
     * - Creates a new DataFrame containing the prediction times and values.
     *
     * Preconditions:
     * - The DataFrame must not be null.
     * - The DataFrame must have more rows than the specified order.
     */
    override fun calculateModel() {
        val dataFrame = getDataFrame()
        if (dataFrame != null && dataFrame.rowsCount() > order) {
            // Extract time and value columns
            val timeColumn = dataFrame["Time"].toList()
            val valueColumn = dataFrame["Price"].toList().map { it.toString().toDouble() }

            // Fit AR model
            _coefficients = fitArModel(valueColumn, order)

            // Generate predictions
            val predictions = generatePredictions(valueColumn, _coefficients!!, predictionHorizon)

            // Create prediction times (continue from last time point)
            val lastTime = timeColumn.last().toString().toDouble()
            val timeStep = if (timeColumn.size > 1) {
                timeColumn[1].toString().toDouble() - timeColumn[0].toString().toDouble()
            } else 1.0

            val predictionTimes = (1..predictionHorizon).map {
                (lastTime + it * timeStep).toString()
            }

            // Create result DataFrame
            _predictionModel = dataFrameOf(
                "Time" to predictionTimes,
                "Prediction" to predictions
            )
        }
    }

    /**
     * Retrieves the calculated AR prediction model as a DataFrame.
     *
     * @return The prediction model DataFrame, or null if the model has not been calculated.
     */
    override fun getModel(): DataFrame<*>? {
        return _predictionModel
    }

    /**
     * Retrieves the coefficients of the AR model.
     *
     * @return An array of AR model coefficients, or null if the model has not been calculated.
     */
    fun getCoefficients(): DoubleArray? {
        return _coefficients
    }

    /**
     * Fits an AR model to the given data using Ordinary Least Squares (OLS) regression.
     *
     * @param data The list of data points to fit the model to.
     * @param order The number of previous data points to use for the AR model.
     * @return An array of regression coefficients, including the intercept.
     */
    private fun fitArModel(data: List<Double>, order: Int): DoubleArray {
        val regression = OLSMultipleLinearRegression()

        // Prepare data for regression
        val n = data.size - order
        val y = DoubleArray(n)
        val x = Array(n) { DoubleArray(order) }

        for (i in 0 until n) {
            y[i] = data[i + order]
            for (j in 0 until order) {
                x[i][j] = data[i + order - j - 1]
            }
        }

        regression.newSampleData(y, x)
        return regression.estimateRegressionParameters()
    }

    /**
     * Generates future predictions based on the AR model coefficients.
     *
     * @param data The list of historical data points.
     * @param coefficients The AR model coefficients, including the intercept.
     * @param horizon The number of future data points to predict.
     * @return A list of predicted values.
     */
    private fun generatePredictions(
        data: List<Double>,
        coefficients: DoubleArray,
        horizon: Int
    ): List<Double> {
        val result = mutableListOf<Double>()
        val workingData = data.toMutableList()

        // Generate predictions one step at a time
        for (i in 0 until horizon) {
            var prediction = coefficients[0] // Intercept

            // Apply AR coefficients to previous values
            for (j in 1 until coefficients.size) {
                prediction += coefficients[j] * workingData[workingData.size - j]
            }

            result.add(prediction)
            workingData.add(prediction) // Add prediction to working data for next step
        }

        return result
    }
}