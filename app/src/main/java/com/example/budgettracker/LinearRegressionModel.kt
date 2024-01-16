package com.example.budgettracker

import kotlin.math.*

class LinearRegressionModel(private val x: DoubleArray, private val y: DoubleArray) {

    private val n: Int = x.size
    private var slope: Double = 0.0
    private var intercept: Double = 0.0

    init {
        if (x.size != y.size) {
            throw IllegalArgumentException("Input arrays must have the same size.")
        }
        train()
    }

    private fun train() {
        val meanX = x.average()
        val meanY = y.average()

        var numerator = 0.0
        var denominator = 0.0

        for (i in 0 until n) {
            numerator += (x[i] - meanX) * (y[i] - meanY)
            denominator += (x[i] - meanX).pow(2)
        }

        slope = numerator / denominator
        intercept = meanY - slope * meanX
    }

    fun predict(inputX: Double): Double {
        return slope * inputX + intercept
    }
}
