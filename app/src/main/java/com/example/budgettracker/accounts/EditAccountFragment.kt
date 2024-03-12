package com.example.budgettracker.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgettracker.R
import com.example.budgettracker.ViewModel
import com.example.budgettracker.databinding.FragmentEditAccountBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditAccountFragment : Fragment() {

    private var _binding : FragmentEditAccountBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        var name = viewModel.accountForChange.name
        var balance = viewModel.accountForChange.balance
        var accountType = viewModel.accountForChange.accountType
        binding.accountType.setText(accountType, false)
        binding.name.setText(name)
        binding.currentBalance.setText(balance)
        binding.savings.isChecked = viewModel.accountForChange.isSavings
        binding.name.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        name = s.toString()
                    }
                    else {
                        name = ""
                    }
                }
            }
        )

        binding.currentBalance.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        balance = s.toString()
                    }
                    else {
                        balance = "0"
                    }
                }
            }
        )
        val typesList = resources.getStringArray(R.array.accountTypes)
        val adapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, typesList)
        binding.accountType.setAdapter(adapter)

        binding.accountType.setOnItemClickListener { parent, view, position, id ->
            accountType = typesList[position]
        }

        binding.save.setOnClickListener {
            var flag = true
            for (element in viewModel.allAccounts.value!!) {
                if (element.name == name && name != viewModel.accountForChange.name) {
                    flag = false
                    break
                }
            }
            if (name.isEmpty() && flag) {
                binding.nameLayout.error = "Please enter a valid, unique name"
            }
            else
            {
                viewModel.deleteAccount(viewModel.accountForChange)
                val editedAccount = AccountsData(0, name, balance, accountType, binding.savings.isChecked)
                viewModel.addAccount(editedAccount)
                viewModel.changeOperationAccount(name, viewModel.accountForChange.name)
                findNavController().navigate(R.id.action_editAccountFragment_to_accounts)
            }

        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete an account?")
                .setMessage("The account and all operations will be deleted. This can't be undone.")
                .setPositiveButton("YES") { dialog, which ->
                    viewModel.deleteAccount(viewModel.accountForChange)
                    viewModel.deleteAccountOperations(viewModel.accountForChange)
                    findNavController().popBackStack()
                }
                .setNegativeButton("NO") {dialog, which ->
                }
                .show()

        }


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}