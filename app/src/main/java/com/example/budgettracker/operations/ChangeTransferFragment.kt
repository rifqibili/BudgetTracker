package com.example.budgettracker.operations

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
import com.example.budgettracker.R
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.databinding.FragmentChangeTransferBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ChangeTransferFragment : Fragment() {

    private var _binding : FragmentChangeTransferBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickedDate: Date
    private var calendar = Calendar.getInstance()
    var amountValue = ""
    var accountName = ""
    //private lateinit var operation : OperationsData
    var transferTo = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentChangeTransferBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        //operation = OperationsData(0, "", 0, "", operationsViewModel.operationForChange.type, calendar.time, "", "", false, 0)
        amountValue = operationsViewModel.operationForChange.amount
        accountName = operationsViewModel.operationForChange.account
        pickedDate = operationsViewModel.operationForChange.date
        transferTo = operationsViewModel.operationForChange.transferTo

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
        operationsViewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            for (element in it){
                if (element.name != accountName && element.name != transferTo)
                    types.add(element.name)
            }
        })


        // choosen[0] - from, choosen[1] - to
        val choosen = arrayOf("", "")
        choosen[0] = accountName
        choosen[1] = transferTo
        binding.accountTypeFrom.setAdapter(ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types))
        binding.accountTypeFrom.setText(accountName, false)
        binding.accountTypeTo.setAdapter(ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types))
        binding.accountTypeTo.setText(transferTo, false)



        binding.accountTypeFrom.setOnItemClickListener { parent, view, position, id ->
            types.add(choosen[0])
            choosen[0] = types[position]
            types.remove(choosen[0])

            val newAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types)
            binding.accountTypeFrom.setAdapter(newAdapter)
            binding.accountTypeTo.setAdapter(newAdapter)
        }

        binding.accountTypeTo.setOnItemClickListener { parent, view, position, id ->
            types.add(choosen[1])
            choosen[1] = types[position]
            types.remove(choosen[1])

            val newAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types)
            binding.accountTypeFrom.setAdapter(newAdapter)
            binding.accountTypeTo.setAdapter(newAdapter)
        }

        binding.swap.setOnClickListener {
            choosen.reverse()
            binding.accountTypeFrom.setText(choosen[0], false)
            binding.accountTypeTo.setText(choosen[1], false)
        }



        binding.save.setOnClickListener {
            operationsViewModel.deleteOperation(operationsViewModel.operationForChange)
            val operation = OperationsData(0, amountValue, R.drawable.up_right_arrow_icon, choosen[1], "Transfer", pickedDate, choosen[0], choosen[1], false, 0)
            operationsViewModel.addOperation(operation)
            findNavController().popBackStack()
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete an operation?")
                .setPositiveButton("YES") { dialog, which ->
                    operationsViewModel.deleteOperation(operationsViewModel.operationForChange)
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