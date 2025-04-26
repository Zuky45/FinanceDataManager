package com.example.datamanager.backend.models

import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoint
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

/**
 * Represents a polynomial approximation model for a given dataset.
 *
 * @property degree The degree of the polynomial used for approximation. Must be >= 1.
 * @constructor Initializes the Approximation model with an optional DataFrame and polynomial degree.
 */
class Approximation(dataFrame: DataFrame<*>? = null, private var degree: Int = 1) : Model("Approximation", dataFrame) {
    private var _approximationModel: DataFrame<*>? = null // Holds the resulting approximation model as a DataFrame.
    private var _coefficients: DoubleArray? = null // Stores the coefficients of the polynomial.

    /**
     * Calculates the polynomial approximation model based on the input DataFrame.
     *
     * - Extracts x and y points from the DataFrame.
     * - Fits a polynomial curve to the data using the specified degree.
     * - Generates a new DataFrame containing the approximated values.
     *
     * Preconditions:
     * - The DataFrame must not be null.
     * - The DataFrame must have at least one row.
     * - The degree must be >= 1.
     */
    override fun calculateModel() {
        val dataFrame = getDataFrame()
        if (dataFrame != null && dataFrame.rowsCount() > 0 && degree >= 1) {
            // Extract x and y points from the DataFrame
            val points = ArrayList<WeightedObservedPoint>()

            for (i in 0 until dataFrame.rowsCount()) {
                val x = i.toDouble()  // Using index as x coordinate
                val price = dataFrame["Price"][i].toString().toDoubleOrNull() ?: 0.0
                points.add(WeightedObservedPoint(1.0, x, price))
            }

            // Create and configure the polynomial curve fitter
            val fitter = PolynomialCurveFitter.create(degree)

            // Perform the fit to get polynomial coefficients
            _coefficients = fitter.fit(points)

            // Create a polynomial function with the coefficients
            val polyFunction = PolynomialFunction(_coefficients)

            // Generate approximation data
            val xValues = List(dataFrame.rowsCount()) { it.toDouble() }
            val yValues = xValues.map { polyFunction.value(it) }

            // Create a new DataFrame with original time and approximated values
            val timeColumn = dataFrame["Time"].map { it }
            val approxValues = yValues.toTypedArray()

            _approximationModel = dataFrameOf("Time" to timeColumn.toList(), "Approximation" to approxValues.toList())
        }
    }

    /**
     * Retrieves the calculated approximation model as a DataFrame.
     *
     * @return The approximation model DataFrame, or null if the model has not been calculated.
     */
    override fun getModel(): DataFrame<*>? {
        return _approximationModel
    }

    /**
     * Retrieves the coefficients of the polynomial used in the approximation.
     *
     * @return An array of polynomial coefficients, or null if the model has not been calculated.
     */
    fun getCoefficients(): DoubleArray? {
        return _coefficients
    }

    fun calculateMSE(): Double {
        val dataFrame = getDataFrame()
        if (dataFrame != null && _approximationModel != null) {
            val originalValues = dataFrame["Price"].toList().mapNotNull { it as? Double }
            val approxValues = _approximationModel!!["Approximation"].toList().mapNotNull { it as? Double }

            var mse = 0.0
            for (i in originalValues.indices) {
                val error = originalValues[i] - approxValues[i]
                mse += error * error
            }

            return mse / originalValues.size
        }
        return 0.0
    }
}