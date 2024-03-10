package com.example.budgettracker.operations.income

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentAddIncomeBinding
import com.example.budgettracker.operations.OperationsData
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.operations.expense.AddData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class IncomeFragment : Fragment() {

    private var _binding : FragmentAddIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var operation : OperationsData
    private lateinit var list: List<AddData>
    private lateinit var pickedDate: Date
    private var calendar = Calendar.getInstance()
    private var accountName = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentAddIncomeBinding.inflate(inflater, container, false)
        val root : View = binding.root


        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        binding.incomeButton.isChecked = true
        binding.incomeButton.setTextColor(Color.WHITE)
        binding.back.setOnClickListener{
            findNavController().navigate(R.id.action_addIncomeFragment_to_operations)
        }
        var amountValue = "0"
        binding.amount.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        amountValue = s.toString()
                    }
                    else { amountValue = "0" }
                }
            }
        )
        var note = ""
        binding.note.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        note = s.toString()
                    }
                    else { note = "" }
                }
            }
        )
        val result = context?.assets
            ?.open("income_categories.json")
            ?.bufferedReader()
            .use { it!!.readText() }
        val gson = GsonBuilder().create()
        list = gson.fromJson(result,Array<AddData>::class.java).toList()


        binding.categorySelect.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.categorySelect.adapter = IncomeAdapter(list, operationsViewModel)
        binding.save.setOnClickListener {
            if (accountName == ""){
                binding.accountChoice.error = "Enter a valid account"
            }
            else {
                operationsViewModel.incomeList.observe(viewLifecycleOwner, Observer {
                    val icon = requireContext().resources.getIdentifier(it.last().categoryIcon, "drawable", context?.packageName)
                    operation = OperationsData(0, amountValue, icon , it.last().categoryName, "Income", pickedDate, accountName, "", false, 0, note)
                    operationsViewModel.addOperation(operation)
                    operationsViewModel.clearIncome()

                })
                findNavController().navigate(R.id.action_addIncomeFragment_to_operations)
            }

        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.expenseButton -> {
                    binding.incomeButton.setTextColor(Color.BLACK)
                    binding.expenseButton.setTextColor(Color.WHITE)
                    binding.transferButton.setTextColor(Color.BLACK)
                    findNavController().navigate(R.id.action_addIncomeFragment_to_addExpenseFragment)
                }
                R.id.transferButton -> {
                    binding.incomeButton.setTextColor(Color.BLACK)
                    binding.expenseButton.setTextColor(Color.BLACK)
                    binding.transferButton.setTextColor(Color.WHITE)
                    findNavController().navigate(R.id.action_addIncomeFragment_to_transferFragment)
                }
            }

        }

        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        pickedDate = today.time
        binding.dateButton.text = dateFormat.format(pickedDate)
        binding.dateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                pickedDate = selectedDate.time
                binding.dateButton.text = dateFormat.format(pickedDate)
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.show()
        }
        val types = ArrayList<String>()
        operationsViewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            for (element in it){
                types.add(element.name)
            }
        })

        val adapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types)
        binding.accountType.setAdapter(adapter)

        binding.accountType.setOnItemClickListener { parent, view, position, id ->
            accountName = types[position]
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}