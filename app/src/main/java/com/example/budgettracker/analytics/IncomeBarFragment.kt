package com.example.budgettracker.analytics

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentIncomeBarBinding
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
import com.google.android.material.color.MaterialColors
import java.util.Calendar

class IncomeBarFragment : Fragment() {
    private var _binding : FragmentIncomeBarBinding? = null
    private val binding get() = _binding!!
    private lateinit var months : Array<String>
    var incomeMap = mutableMapOf<Float, Float>()
    val calendar = Calendar.getInstance()
    val existingYears = ArrayList<Int>()
    lateinit var labels : Array<String>
    var selectedYear = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentIncomeBarBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val customColors = intArrayOf(
            ColorTemplate.rgb("#00BCD4") // blue
        )
        months = resources.getStringArray(R.array.shortMonths)
        labels = months
        operationsViewModel.divideIncomes(operationsViewModel.operationsList.value!!)

        val incomeDataList = operationsViewModel.divideOperationsByMonth(operationsViewModel.allIncomes)
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
                val y = e.y
                val selectedXAxisCount = x.substringBefore(".") //this value is float so use substringbefore method
                // another method shown below
                val nonFloat = binding.barChart.xAxis.valueFormatter.getFormattedValue(e.x)
                binding.yearText.text = selectedXAxisCount
            }

            override fun onNothingSelected() { }

        })



        return root
    }

    private fun setupBar(selectedYear : Int, expenseDataList : ArrayList<ArrayList<OperationsData>>, customColors : IntArray) {
        val entries = ArrayList<BarEntry>()
        incomeMap = mutableMapOf()


        if (expenseDataList.isNotEmpty()) {
            for (i in 0 until expenseDataList.size) {
                calendar.time = expenseDataList[i][0].date
                val month = calendar.get(Calendar.MONTH).toFloat()
                val year = calendar.get(Calendar.YEAR)
                existingYears.add(year)
                if (year == selectedYear) {
                    for (j in 0 until expenseDataList[i].size) {
                        val amount = expenseDataList[i][j].amount.toInt()
                        incomeMap[month] = incomeMap.getOrDefault(month, 0f) + amount
                    }
                }
            }
        }

        else {

        }

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

        //binding.barChart.animateY(1000)

        binding.barChart.xAxis.gridColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, Color.BLACK)
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