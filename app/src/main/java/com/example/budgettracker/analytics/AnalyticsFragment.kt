package com.example.budgettracker.analytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAnalyticsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator


class AnalyticsFragment : Fragment() {

    private var _binding : FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE

        val fragmentList = listOf(ExpensePieFragment(), IncomePieFragment(), ExpenseBarFragment(), IncomeBarFragment())
        val tabsText = resources.getStringArray(R.array.tabs)

        var adapter = AnalyticsAdapter(requireActivity(), fragmentList)
        binding.vp2.adapter = adapter

        operationsViewModel.operationsList.observe(viewLifecycleOwner, Observer {
            adapter = AnalyticsAdapter(requireActivity(), fragmentList)
            binding.vp2.adapter = adapter
        })

        TabLayoutMediator(binding.tabLayout, binding.vp2) {
            tab, pos -> tab.text = tabsText[pos]
        }.attach()

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}