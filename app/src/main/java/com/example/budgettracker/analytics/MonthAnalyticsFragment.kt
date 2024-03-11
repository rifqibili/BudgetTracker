package com.example.budgettracker.analytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentCategoryAnalyticsBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.OperationsData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar


class MonthAnalyticsFragment : Fragment() {

    private var _binding : FragmentCategoryAnalyticsBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentCategoryAnalyticsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE
        binding.categoryOperationsRV.layoutManager = LinearLayoutManager(context)

        val months = resources.getStringArray(R.array.months)

        binding.categoryName.text = ""
        binding.monthText.text = "Total for ${months[operationsViewModel.analyzedMonthIndex]}"


        operationsViewModel.operationsList.observe(viewLifecycleOwner, Observer {
            val monthOperations = ArrayList<OperationsData>()
            var operationsByMonth = ArrayList<ArrayList<OperationsData>>()
            when (operationsViewModel.typeOfAnalyzedOperation) {
                "Income" -> { operationsByMonth = operationsViewModel.divideOperationsByMonth(operationsViewModel.allIncomes) }
                "Expense" -> { operationsByMonth = operationsViewModel.divideOperationsByMonth(operationsViewModel.allExpenses) }
            }
            for (operationsForMonth in operationsByMonth) {
                calendar.time = operationsForMonth[0].date
                Log.d("TAG", calendar.get(Calendar.MONTH).toString())
                Log.d("TAG", calendar.get(Calendar.YEAR).toString())
                if (calendar.get(Calendar.MONTH) == operationsViewModel.analyzedMonthIndex
                    && calendar.get(Calendar.YEAR) == operationsViewModel.selectedYear) {
                    for (element in operationsForMonth) {
                        monthOperations.add(element)
                    }
                }
            }
            var totalAmount = monthOperations.sumOf { it.amount.toInt() }
            binding.monthTotal.text = totalAmount.toString()
            binding.categoryOperationsRV.adapter = OperationsAdapter(monthOperations, findNavController(), operationsViewModel)
        })

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}