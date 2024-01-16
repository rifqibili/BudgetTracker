package com.example.budgettracker.operations

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgettracker.OperationsViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentInformationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class InformationFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val operationsViewModel = ViewModelProvider(requireActivity()).get(OperationsViewModel::class.java)
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root : View = binding.root
        val selectedOperation = operationsViewModel.listForInformation[operationsViewModel.selectedOperationIndex]
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.icon.setImageResource(selectedOperation.icon)
        when(selectedOperation.type) {
            "Income" -> {
                binding.amount.text = "+" + selectedOperation.amount
                binding.amount.setTextColor(Color.GREEN)
            }
            "Expense" -> {
                binding.amount.text = "-" + selectedOperation.amount
                binding.amount.setTextColor(Color.RED)
            }
            "Transfer" -> binding.amount.text = selectedOperation.amount
        }
        binding.date.text = dateFormat.format(selectedOperation.date)
        binding.account.text = selectedOperation.account
        binding.category.text = selectedOperation.category

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete an operation?")
                .setPositiveButton("YES") { dialog, which ->
                    operationsViewModel.deleteOperation(selectedOperation)
                    dismiss()
                }
                .setNegativeButton("NO") {dialog, which ->
                }
                .show()
        }

        binding.changeButton.setOnClickListener {
            operationsViewModel.operationForChange = selectedOperation
            if (selectedOperation.type == "Transfer")
                findNavController().navigate(R.id.action_informationFragment_to_changeTransferFragment)
            else
                findNavController().navigate(R.id.action_informationFragment_to_changeOperationFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}