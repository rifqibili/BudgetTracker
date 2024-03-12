package com.example.budgettracker.operations.transfer

import android.app.DatePickerDialog
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
import com.example.budgettracker.operations.OperationsData
import com.example.budgettracker.ViewModel
import com.example.budgettracker.databinding.FragmentEditOperationBinding
import com.example.budgettracker.operations.expense.AddData
import com.example.budgettracker.operations.expense.ExpenseAdapter

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class EditOperationFragment : Fragment() {

    private var _binding : FragmentEditOperationBinding? = null
    private val binding get() = _binding!!
    private lateinit var operation : OperationsData
    private lateinit var list: MutableList<AddData>
    private var calendar = Calendar.getInstance()
    private lateinit var pickedDate : Date
    private var accountName = ""
    private var amountValue = "0"
    private var noteText = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentEditOperationBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        binding.back.setOnClickListener{
            findNavController().popBackStack()
        }
        operation = OperationsData(0, "", 0, "",
            viewModel.operationForChange.type, calendar.time, "", "", false, 0, "")
        amountValue = viewModel.operationForChange.amount
        accountName = viewModel.operationForChange.account
        pickedDate = viewModel.operationForChange.date
        var category = viewModel.operationForChange.category
        noteText = viewModel.operationForChange.note

        binding.amount.setText(amountValue)
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
        binding.note.setText(noteText)
        binding.note.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        noteText = s.toString()
                    }
                    else { noteText = "" }
                }
            }
        )
        var result = context?.assets
            ?.open("expense_categories.json")
            ?.bufferedReader()
            .use { it!!.readText() }
        if (operation.type == "Income") {
            result = context?.assets
                ?.open("income_categories.json")
                ?.bufferedReader()
                .use { it!!.readText() }
        }
        val gson = GsonBuilder().create()
        list = gson.fromJson(result,Array<AddData>::class.java).toMutableList()


        binding.categorySelect.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        if (operation.type == "Income")
            binding.categorySelect.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        for (element in list) {
            if (element.categoryName == category) {
                list.remove(element)
                list.add(0, element)
                break
            }
        }
        binding.categorySelect.adapter = ExpenseAdapter(list, viewModel)
        binding.save.setOnClickListener {
            if (accountName == ""){
                binding.accountChoice.error = "Enter a valid account"
            }
            else{
                viewModel.expenseList.observe(viewLifecycleOwner, Observer {
                    val icon = requireContext().resources.getIdentifier(it.last().categoryIcon, "drawable", context?.packageName)

                    operation.account = accountName
                    operation.category = it.last().categoryName
                    operation.icon = icon
                    operation.date = pickedDate
                    operation.amount = amountValue
                    operation.note = noteText

                    viewModel.deleteOperation(viewModel.operationForChange)
                    viewModel.addOperation(operation)

                    viewModel.clearExpense()
                })
                findNavController().popBackStack()
            }

        }

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
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

        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            types.clear()
            for (element in it){
                types.add(element.name)
            }
        })


        val adapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types)
        binding.accountType.setAdapter(adapter)

        binding.accountType.setText(accountName, false)
        binding.accountType.setOnItemClickListener { parent, view, position, id ->
            accountName = types[position]
        }

        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete an operation?")
                .setPositiveButton("YES") { dialog, which ->
                    viewModel.deleteOperation(viewModel.operationForChange)
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