package com.example.budgettracker.accounts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAccountInformationBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.OperationsData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountInformationFragment : Fragment() {

    private var _binding : FragmentAccountInformationBinding? = null
    private val binding get() = _binding!!
    lateinit var lastMonthOperations : ArrayList<OperationsData>
    lateinit var currentAccount : AccountsData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentAccountInformationBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        if (viewModel.isSavingsSelected)
            currentAccount = viewModel.savingsAccounts[viewModel.selectedAccountIndex]
        else
            currentAccount = viewModel.paymentAccounts[viewModel.selectedAccountIndex]
        binding.accountOperationsList.layoutManager = LinearLayoutManager(context)
        val currentAccountOperations = arrayListOf<OperationsData>()

        viewModel.operationsList.observe(viewLifecycleOwner, Observer {
            currentAccountOperations.clear()
            for (i in 0 until viewModel.operationsList.value!!.size) {
                if (currentAccount.name == viewModel.operationsList.value!![i].account) {
                    currentAccountOperations.add(viewModel.operationsList.value!![i])
                }
            }
            binding.accountOperationsList.adapter = OperationsAdapter(currentAccountOperations, findNavController(), viewModel)
            var currentAccountIncome = 0.0
            var currentAccountExpense = 0.0
            if (currentAccountOperations.isNotEmpty()) {
                lastMonthOperations = viewModel.divideOperationsByMonth(currentAccountOperations)[0]
                for (element in lastMonthOperations) {
                    when(element.type) {
                        "Income" -> currentAccountIncome += element.amount.toDouble()
                        "Expense" -> currentAccountExpense += element.amount.toDouble()
                    }
                }
            }
            when(currentAccount.accountType) {
                "Cash" -> binding.icon.setImageResource(R.drawable.money_icon)
                "Card" -> binding.icon.setImageResource(R.drawable.card_icon)
                "Bank account" -> binding.icon.setImageResource(R.drawable.bank_icon)
            }
            binding.accountName.text = currentAccount.name
            binding.accountBalance.text = currentAccount.balance
            binding.incomeText.text = "Income\n$currentAccountIncome"
            binding.expenseText.text = "Expense\n$currentAccountExpense"
        })





        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete an account?")
                .setMessage("The account and all operations will be deleted. This can't be undone.")
                .setPositiveButton("YES") { dialog, which ->
                    viewModel.deleteAccount(currentAccount)
                    viewModel.deleteAccountOperations(currentAccount)
                    findNavController().popBackStack()
                }
                .setNegativeButton("NO") {dialog, which ->
                }
                .show()

        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.settings.setOnClickListener {
            viewModel.accountForChange = currentAccount
            findNavController().navigate(R.id.action_accountInformationFragment_to_editAccountFragment)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}