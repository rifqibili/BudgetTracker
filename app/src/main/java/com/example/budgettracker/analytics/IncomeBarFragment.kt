package com.example.budgettracker.analytics

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgettracker.LinearRegressionModel
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentBarBinding
import com.example.budgettracker.operations.OperationsData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Calendar

class IncomeBarFragment : Fragment() {
    private var _binding : FragmentBarBinding? = null
    private val binding get() = _binding!!
    private lateinit var months : Array<String>
    var incomeMap = mutableMapOf<Float, Float>()
    val calendar = Calendar.getInstance()
    val existingYears = ArrayList<Int>()
    lateinit var labels : Array<String>
    var selectedYear = -1
    val incomeEveryMonth = arrayListOf<Double>()
    val allMonths = arrayListOf<Double>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentBarBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val customColors = intArrayOf(
            ColorTemplate.rgb("#00BCD4") // blue
        )
        months = resources.getStringArray(R.array.shortMonths)
        labels = months
        viewModel.divideIncomes(viewModel.operationsList.value!!)

        val incomeDataList = viewModel.divideOperationsByMonth(viewModel.allIncomes)
        //var selectedYear = calendar.get(Calendar.YEAR)
        if (incomeDataList.isNotEmpty()) {
            calendar.time = incomeDataList[0][0].date
            selectedYear = calendar.get(Calendar.YEAR)
            binding.yearText.text = selectedYear.toString()
        }
        else {
            binding.yearText.text = "No data avalible"
        }

        setupBar(selectedYear, incomeDataList, customColors)

        binding.yearBack.setOnClickListener {
            if (selectedYear - 1 in existingYears) {
                selectedYear--
                setupBar(selectedYear, incomeDataList, customColors)
                binding.yearText.text = selectedYear.toString()
            }
        }
        binding.yearForward.setOnClickListener {
            if (selectedYear + 1 in existingYears) {
                selectedYear++
                setupBar(selectedYear, incomeDataList, customColors)
                binding.yearText.text = selectedYear.toString()
            }
        }

        binding.barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight?) {
                val x = e.x.toString()
                val selectedXAxisCount = x.substringBefore(".") //this value is float so use substringbefore method
                // another method shown below
                val nonFloat = binding.barChart.xAxis.valueFormatter.getFormattedValue(e.x)
                //binding.yearText.text = selectedXAxisCount
                viewModel.analyzedMonthIndexForBar = selectedXAxisCount.toInt()
                viewModel.selectedYear = selectedYear
                //viewModel.typeOfAnalyzedOperation.value = 3
                findNavController().navigate(R.id.action_analytics_to_monthAnalyticsFragment)
            }

            override fun onNothingSelected() { }

        })



        return root
    }

    private fun setupBar(selectedYear : Int, incomeDataList : ArrayList<ArrayList<OperationsData>>, customColors : IntArray) {
        val entries = ArrayList<BarEntry>()
        incomeMap = mutableMapOf()

        var index = 0.0
        incomeEveryMonth.clear()
        allMonths.clear()
        if (incomeDataList.isNotEmpty()) {
            for (i in 0 until incomeDataList.size) {
                calendar.time = incomeDataList[i][0].date
                val month = calendar.get(Calendar.MONTH).toFloat()
                val year = calendar.get(Calendar.YEAR)
                existingYears.add(year)
                incomeEveryMonth.add(incomeDataList[i].sumOf { it.amount.toDouble() })
                index += 1.0
                allMonths.add(index)
                if (year == selectedYear) {
                    for (j in 0 until incomeDataList[i].size) {
                        val amount = incomeDataList[i][j].amount.toInt()
                        incomeMap[month] = incomeMap.getOrDefault(month, 0f) + amount
                    }
                }
            }
        }

        else {

        }
        allMonths.reverse()
        val linearRegressionModel = LinearRegressionModel(allMonths.toDoubleArray(), incomeEveryMonth.toDoubleArray())
        val nextMonth = index + 1.0
        val predictedExpense = linearRegressionModel.predict(nextMonth)
        if (allMonths.size > 1 && predictedExpense >= 0)
            binding.prediction.text = "%.1f".format(predictedExpense)
        else
            binding.prediction.text = "Not enough data"


        incomeMap.forEach { (month, value) ->
            entries.add(BarEntry(month, value))
        }

        entries.reverse()

        val barDataSet = BarDataSet(entries, "")

        barDataSet.setColors(customColors, 255)
        barDataSet.valueFormatter = LargeValueFormatter()
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false

        binding.barChart.animateY(1000)
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.xAxis.gridColor = Color.WHITE
        binding.barChart.axisLeft.isEnabled = false
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.labelCount = labels.size
        binding.barChart.isDoubleTapToZoomEnabled = false
        binding.barChart.isScaleXEnabled = false
        binding.barChart.isScaleYEnabled = false
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.isGranularityEnabled = true

        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}