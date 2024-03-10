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