package com.example.budgettracker.accounts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAccountsBinding
import com.example.budgettracker.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountsFragment : Fragment() {

    private var _binding : FragmentAccountsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE
        viewModel.lastExpenseMonthIndex = 0
        viewModel.lastIncomeMonthIndex = 0
        binding.addAccount.setOnClickListener { findNavController().navigate(R.id.action_accounts_to_addAccountFragment) }

        binding.accountsRV.layoutManager = LinearLayoutManager(context)

        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            viewModel.total()
            binding.total.text = viewModel.totalSum.toString()
            viewModel.divideAccounts()
            binding.accountsRV.adapter = AccountsAdapter(viewModel.paymentAccounts, findNavController(), viewModel)
            binding.savingsRV.adapter = AccountsAdapter(viewModel.savingsAccounts, findNavController(), viewModel)

        })


        binding.savingsRV.layoutManager = LinearLayoutManager(context)


        viewModel.totalSum.observe(viewLifecycleOwner, Observer {
            binding.total.text = it.toString()
        })



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}