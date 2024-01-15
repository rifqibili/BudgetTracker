package com.example.budgettracker.accounts

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
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAccountsBinding
import com.example.budgettracker.OperationsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountsFragment : Fragment() {

    private var _binding : FragmentAccountsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root : View = binding.root


        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE


        binding.addAccount.setOnClickListener { findNavController().navigate(R.id.action_accounts_to_addAccountFragment) }

        binding.accountsRV.layoutManager = LinearLayoutManager(context)


        operationsViewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            operationsViewModel.total()
            binding.total.text = operationsViewModel.totalSum.toString()
            operationsViewModel.divideAccounts()
            binding.accountsRV.adapter = AccountsAdapter(operationsViewModel.paymentAccounts, findNavController(), operationsViewModel)
            binding.savingsRV.adapter = AccountsAdapter(operationsViewModel.savingsAccounts, findNavController(), operationsViewModel)

        })


        binding.savingsRV.layoutManager = LinearLayoutManager(context)


        operationsViewModel.totalSum.observe(viewLifecycleOwner, Observer {
            binding.total.text = it.toString()
        })



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}