package com.example.datamanager.backend.models

import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoint
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

class Approximation(dataFrame: DataFrame<*>? = null, private var degree: Int = 1) : Model("Approximation", dataFrame) {
    private var _approximationModel: DataFrame<*>? = null
    private var _coefficients: DoubleArray? = null

    override fun calculateModel() {
        val dataFrame = getDataFrame()
        if (dataFrame != null && dataFrame.rowsCount() > 0 && degree >= 1) {
            // Extract x and y points from dataframe
            val points = ArrayList<WeightedObservedPoint>()

            for (i in 0 until dataFrame.rowsCount()) {
                val x = i.toDouble()  // Using index as x coordinate
                val price = dataFrame["Price"][i].toString().toDoubleOrNull() ?: 0.0
                points.add(WeightedObservedPoint(1.0, x, price))
            }

            // Create and configure the fitter
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

    override fun getModel(): DataFrame<*>? {
        return _approximationModel
    }

    fun getCoefficients(): DoubleArray? {
        return _coefficients
    }
}