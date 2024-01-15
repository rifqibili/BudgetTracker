package com.example.budgettracker.accounts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAccountInformationBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.OperationsData
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountInformationFragment : Fragment() {

    private var _binding : FragmentAccountInformationBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentAccountInformationBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        val currentAccount = operationsViewModel.allAccounts.value!![operationsViewModel.selectedAccount]
        binding.accountOperationsList.layoutManager = LinearLayoutManager(context)
        val currentAccountOperations = arrayListOf<OperationsData>()
        for (i in 0 until operationsViewModel.operationsList.value!!.size) {
            if (currentAccount.name == operationsViewModel.operationsList.value!![i].account) {
                currentAccountOperations.add(operationsViewModel.operationsList.value!![i])
            }
        }
        binding.accountOperationsList.adapter = OperationsAdapter(currentAccountOperations)

        var currentAccountIncome = 0
        var currentAccountExpense = 0
        val lastMonthOperations = operationsViewModel.divideOperationsByMonth(currentAccountOperations)[0]
        for (element in lastMonthOperations) {
            when(element.type) {
                "Income" -> currentAccountIncome += element.amount.toInt()
                "Expense" -> currentAccountExpense += element.amount.toInt()
            }
        }

        binding.accountName.text = currentAccount.name
        binding.accountBalance.text = currentAccount.balance
        binding.incomeText.text = "Income\n$currentAccountIncome"
        binding.expenseText.text = "Expense\n$currentAccountExpense"



        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_accountInformationFragment_to_accounts)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}