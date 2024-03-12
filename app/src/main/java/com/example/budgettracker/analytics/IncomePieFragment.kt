package com.example.budgettracker.analytics

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentIncomePieBinding
import com.example.budgettracker.operations.OperationsData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate.rgb
import java.util.Calendar

class IncomePieFragment : Fragment() {

    private var _binding : FragmentIncomePieBinding? = null
    private val binding get() = _binding!!
    private lateinit var months : Array<String>
    var categoriesMap = mutableMapOf<String, Pair<Float, Int>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentIncomePieBinding.inflate(inflater, container, false)
        val root : View = binding.root

        months = resources.getStringArray(R.array.months)

        val customColors = intArrayOf(
            rgb("#4CAF50"), // green
            rgb("#FFC107"), // light orange
            rgb("#F44336"), // red
            rgb("#00BCD4"), // blue
            rgb("#9C27B0"), // violet
            rgb("#FF9800"), // orange
            rgb("#3F51B5"), // dark blue
            rgb("#009688"), // dark green
            rgb("#FF5722"), // dark orange
            rgb("#E91E63"), // pink
            rgb("#8BC34A"), // light green
            rgb("#CDDC39"), // light light green
            rgb("#FF36BC"), // light pink
            rgb("#FFEB3B") // yellow
        )

        viewModel.divideIncomes(viewModel.operationsList.value!!)
        var incomeDataList = viewModel.divideOperationsByMonth(viewModel.allIncomes)
        viewModel.operationsList.observe(viewLifecycleOwner, Observer {
            viewModel.divideIncomes(viewModel.operationsList.value!!)
            incomeDataList = viewModel.divideOperationsByMonth(viewModel.allIncomes)
            viewModel.analyzedOperationsListIncome = incomeDataList
        })

        var selectedMonthIndex = viewModel.lastIncomeMonthIndex /////////// ТУТ ЧТО ТО!!!!!!!!!!!!!
        setupPie(selectedMonthIndex, incomeDataList, customColors)

        if (incomeDataList.isNotEmpty()) {
            val resultList = viewModel.collectByCategory(incomeDataList[selectedMonthIndex])
            for (i in 0 until resultList.size) {
                resultList[i].color = customColors[i % customColors.size]
            }
            resultList.sortByDescending { it.amount.toInt() }
            binding.operationsRV.adapter = PieAdapter(resultList, findNavController(), viewModel)
            viewModel.analyzedCategoriesListIncome = resultList
            viewModel.analyzedMonthIndexIncome = selectedMonthIndex
        }

        binding.operationsRV.layoutManager = LinearLayoutManager(context)
        binding.monthBack.setOnClickListener {
            if (selectedMonthIndex != incomeDataList.size -1 && selectedMonthIndex != incomeDataList.size) {
                selectedMonthIndex++
                viewModel.lastIncomeMonthIndex++
                setupPie(selectedMonthIndex, incomeDataList, customColors)
                val resultList = viewModel.collectByCategory(incomeDataList[selectedMonthIndex])
                for (i in 0 until resultList.size) {
                    resultList[i].color = customColors[i]
                }
                resultList.sortByDescending { it.amount.toInt() }
                binding.operationsRV.adapter = PieAdapter(resultList, findNavController(), viewModel)
                viewModel.analyzedCategoriesListIncome = resultList
                viewModel.analyzedMonthIndexIncome = selectedMonthIndex
            }
        }

        binding.monthForward.setOnClickListener {
            if (selectedMonthIndex != 0) {
                selectedMonthIndex--
                viewModel.lastIncomeMonthIndex--
                setupPie(selectedMonthIndex, incomeDataList, customColors)
                val resultList = viewModel.collectByCategory(incomeDataList[selectedMonthIndex])
                for (i in 0 until resultList.size) {
                    resultList[i].color = customColors[i]
                }
                resultList.sortByDescending { it.amount.toInt() }
                binding.operationsRV.adapter = PieAdapter(resultList, findNavController(), viewModel)
                viewModel.analyzedCategoriesListIncome = resultList
                viewModel.analyzedMonthIndexIncome = selectedMonthIndex
            }
        }


        return root
    }

    private fun setupPie(selectedMonthIndex : Int, incomeDataList : ArrayList<ArrayList<OperationsData>>, customColors : IntArray) {
        val entries = ArrayList<PieEntry>()
        categoriesMap = mutableMapOf()
        var totalForMonth = 0

        val calendar = Calendar.getInstance()
        if (incomeDataList.isNotEmpty()) {
            calendar.time = incomeDataList[selectedMonthIndex][0].date
            val month = months[calendar.get(Calendar.MONTH)]
            var year = calendar.get(Calendar.YEAR).toString().substring(2, 4)
            binding.monthText.text = "$month $year"

            for (i in 0 until incomeDataList[selectedMonthIndex].size) {
                val category = incomeDataList[selectedMonthIndex][i].category
                val amount = incomeDataList[selectedMonthIndex][i].amount.toFloat()
                val color = customColors[i % customColors.size]
                categoriesMap[category] = Pair(categoriesMap.getOrDefault(category, Pair(0f, color)).first + amount, color)
                totalForMonth += amount.toInt()
            }
        }

        else {
            binding.monthText.text = "No data avalible"
        }

        categoriesMap.forEach { (category, value) ->
            entries.add(PieEntry(value.first, category))
        }


        val pieDataSet = PieDataSet(entries, "")

        pieDataSet.setColors(customColors, 255)
        pieDataSet.setValueTextColors(mutableListOf(Color.BLACK))

        pieDataSet.valueTextSize = 0f

        val pieData = PieData(pieDataSet)
        binding.pieChart.setTouchEnabled(false)
        binding.pieChart.data = pieData
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.setCenterTextSize(24f)
        if (totalForMonth != 0) {
            binding.pieChart.centerText = totalForMonth.toString()
        }
        binding.pieChart.animateY(1000)
        binding.pieChart.invalidate()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}