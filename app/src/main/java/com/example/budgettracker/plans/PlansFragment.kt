package com.example.budgettracker.plans

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.budgettracker.R
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.ViewModel
import com.example.budgettracker.databinding.FragmentPlansBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.expense.AddData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder

class PlansFragment : Fragment() {

    private var _binding : FragmentPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: MutableList<AddData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        _binding = FragmentPlansBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE
        viewModel.lastExpenseMonthIndex = 0
        viewModel.lastIncomeMonthIndex = 0

        val result = context?.assets
            ?.open("expense_categories.json")
            ?.bufferedReader()
            .use { it!!.readText() }
        val gson = GsonBuilder().create()
        list = gson.fromJson(result,Array<AddData>::class.java).toMutableList()

        viewModel.allLimits.observe(viewLifecycleOwner, Observer{
            for (element in viewModel.allLimits.value!!) {
                for (i in 0 until list.size) {
                    if (element.categoryName == list[i].categoryName) {
                        list.removeAt(i)
                        break
                    }
                }
            }
        })


        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.limits_alert_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        binding.addLimit.setOnClickListener {
            dialog.show()
        }

        val amountField = dialog.findViewById<EditText>(R.id.amount)
        var limitAmount = "0"
        amountField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()){
                    limitAmount = s.toString()
                }
                else { limitAmount = "0" }
            }
        })

        val spinner = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.categorySelect)
        val spinnerField = dialog.findViewById<TextInputLayout>(R.id.categorySelectField)
        val adapter = CategoriesSpinnerAdapter(requireContext(), list)
        spinner.setAdapter(adapter)

        val selectedAddData = AddData("", "", true)
        spinner.setOnItemClickListener { _, _, position, _ ->
            selectedAddData.categoryIcon = adapter.getItem(position)!!.categoryIcon
            selectedAddData.categoryName = adapter.getItem(position)!!.categoryName
            //selectedAddData = adapter.getItem(position)
            spinner.setText(selectedAddData.categoryName, false)
        }

        val cancel = dialog.findViewById<Button>(R.id.cancel)
        cancel.setOnClickListener {
            amountField.setText("")
            spinner.setText("", false)
            dialog.dismiss()
        }

        val add = dialog.findViewById<Button>(R.id.add)
        add.setOnClickListener {
            if (selectedAddData.categoryName != "") {
                val newLimit = LimitsData(0, limitAmount.toDouble(), selectedAddData.categoryIcon, selectedAddData.categoryName)
                viewModel.addLimit(newLimit)
                amountField.setText("")
                spinner.setText("", false)
                dialog.dismiss()
            }
            else {
                spinnerField.error = "Select a category"
            }

        }


        binding.limitsRV.layoutManager = LinearLayoutManager(context)
        viewModel.allLimits.observe(viewLifecycleOwner, Observer {
            binding.limitsRV.adapter = LimitsAdapter(viewModel.allLimits.value!!, findNavController(), viewModel)
        })





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}