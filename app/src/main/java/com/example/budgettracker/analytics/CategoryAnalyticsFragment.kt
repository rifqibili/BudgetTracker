package com.example.budgettracker.analytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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


class CategoryAnalyticsFragment : Fragment() {

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

        val months = resources.getStringArray(R.array.months)

        val analyzedCategory = operationsViewModel.analyzedCategoriesList[operationsViewModel.analyzedCategoryIndex]

        binding.categoryName.text = analyzedCategory.category
        binding.categoryOperationsRV.layoutManager = LinearLayoutManager(context)
        val categoryOperations = ArrayList<OperationsData>()
        operationsViewModel.operationsList.observe(viewLifecycleOwner, Observer {
            categoryOperations.clear()
            for (element in operationsViewModel.analyzedOperationsList[operationsViewModel.analyzedMonthIndex]) {
                if (element.category == analyzedCategory.category) {
                    categoryOperations.add(element)
                }
            }


            calendar.time = categoryOperations[0].date
            val month = calendar.get(Calendar.MONTH)
            binding.monthText.text = "Total for ${months[month]}"
            var totalAmount = categoryOperations.sumOf { it.amount.toInt() }
            binding.monthTotal.text = totalAmount.toString()
            binding.categoryOperationsRV.adapter = OperationsAdapter(categoryOperations, findNavController(), operationsViewModel)
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