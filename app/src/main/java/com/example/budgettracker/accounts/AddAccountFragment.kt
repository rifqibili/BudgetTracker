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
import com.example.budgettracker.databinding.FragmentAddAccountBinding
import com.example.budgettracker.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddAccountFragment : Fragment() {

    private var _binding : FragmentAddAccountBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        var name = ""
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

        var balance = "0"
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

        binding.accountType.setText("Cash", false)
        var accountType = "Cash"
        binding.accountType.setOnItemClickListener { parent, view, position, id ->
            accountType = typesList[position]
        }

        binding.save.setOnClickListener {
            if (name.isEmpty() || !viewModel.isUniqueAccountName(name)) {
                binding.nameLayout.error = "Please enter a valid, unique name"
            }
            else
            {
                viewModel.addAccount(AccountsData(0, name, balance, accountType, binding.savings.isChecked))
                findNavController().popBackStack()
            }

        }
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