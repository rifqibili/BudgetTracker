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
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentCategoryAnalyticsBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.OperationsData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.util.Calendar
import java.util.Date


class CategoryAnalyticsFragment : Fragment() {

    private var _binding : FragmentCategoryAnalyticsBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentCategoryAnalyticsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        val months = resources.getStringArray(R.array.months)


        var analyzedCategory = OperationsData(0, "", 1, "", "", Calendar.getInstance().time, "", "", false, 1, "")
        when (viewModel.typeOfAnalyzedOperation) {
            0 -> {analyzedCategory = viewModel.analyzedCategoriesListExpense[viewModel.analyzedCategoryIndexExpense]}
            1 -> {analyzedCategory = viewModel.analyzedCategoriesListIncome[viewModel.analyzedCategoryIndexIncome]}
        }


        binding.categoryName.text = analyzedCategory.category
        binding.categoryOperationsRV.layoutManager = LinearLayoutManager(context)
        val categoryOperations = ArrayList<OperationsData>()


        viewModel.operationsList.observe(viewLifecycleOwner, Observer {
            if (viewModel.analyzedMonthIndexExpense >= viewModel.analyzedOperationsListExpense.size
                && viewModel.analyzedMonthIndexExpense != 0) {
                viewModel.analyzedMonthIndexExpense--
                viewModel.lastExpenseMonthIndex--
            }
            if (viewModel.analyzedMonthIndexIncome >= viewModel.analyzedOperationsListIncome.size
                && viewModel.analyzedMonthIndexIncome != 0) {
                viewModel.analyzedMonthIndexIncome--
                viewModel.lastIncomeMonthIndex--
            }
            categoryOperations.clear()
            var totalAmount = 0.0
            when (viewModel.typeOfAnalyzedOperation) {
                0 -> {
                    if (viewModel.analyzedOperationsListExpense.size != 0) {
                        for (element in viewModel.analyzedOperationsListExpense[viewModel.analyzedMonthIndexExpense]) {
                            if (element.category == analyzedCategory.category) {
                                categoryOperations.add(element)
                                calendar.time = categoryOperations[0].date
                            }
                        }
                        totalAmount = categoryOperations.sumOf { it.amount.toDouble() }


                    }

                }
                1 -> {
                    if (viewModel.analyzedOperationsListIncome.size != 0) {
                        for (element in viewModel.analyzedOperationsListIncome[viewModel.analyzedMonthIndexIncome]) {
                            if (element.category == analyzedCategory.category) {
                                categoryOperations.add(element)
                                calendar.time = categoryOperations[0].date
                            }
                        }

                        totalAmount = categoryOperations.sumOf { it.amount.toDouble() }

                    }
                }
            }



            val month = calendar.get(Calendar.MONTH)
            binding.monthText.text = "Total for ${months[month]}"

            binding.monthTotal.text = totalAmount.toString()
            binding.categoryOperationsRV.adapter = OperationsAdapter(categoryOperations, findNavController(), viewModel)
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