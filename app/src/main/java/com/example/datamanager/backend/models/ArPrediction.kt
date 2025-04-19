package com.example.datamanager.backend.models

import com.example.datamanager.backend.api_manager.ApiManager
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

class ArPrediction(dataFrame: DataFrame<*>? = null,
                   private var order: Int = 5,
                   private var predictionHorizon: Int = 10) : Model("AR Prediction", dataFrame) {

    private var _predictionModel: DataFrame<*>? = null
    private var _coefficients: DoubleArray? = null

    fun setOrder(order: Int) {
        if (order < 1) throw IllegalArgumentException("Order must be positive")
        this.order = order
    }

    fun setPredictionHorizon(horizon: Int) {
        if (horizon < 1) throw IllegalArgumentException("Prediction horizon must be positive")
        this.predictionHorizon = horizon
    }

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

    override fun getModel(): DataFrame<*>? {
        return _predictionModel
    }

    fun getCoefficients(): DoubleArray? {
        return _coefficients
    }

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
fun main() {
    kotlinx.coroutines.runBlocking {
        // Fetch real stock data
        val apiManager = ApiManager()
        val dataFrame = apiManager.fetchData("AAPL")

        if (dataFrame != null) {
            println("Fetched data: ${dataFrame.rowsCount()} rows")

            // Create AR prediction model
            val arModel = ArPrediction(dataFrame)

            // Set parameters
            arModel.setOrder(5)  // Use 5 previous values
            arModel.setPredictionHorizon(10)  // Predict 10 steps ahead

            // Calculate model
            arModel.calculateModel()

            // Print results
            println("AR Model Coefficients: ${arModel.getCoefficients()?.joinToString()}")
            println("Predictions:")
            arModel.getModel()?.let { predictions ->
                for (i in 0 until predictions.rowsCount()) {
                    println("Time: ${predictions["Time"][i]}, Predicted Value: ${predictions["Prediction"][i]}")
                }
            }
        } else {
            println("Failed to fetch data")
        }
    }
}