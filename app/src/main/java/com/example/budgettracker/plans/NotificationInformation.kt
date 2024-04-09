package com.example.budgettracker.plans

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.databinding.FragmentNotificationInformationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationInformation : BottomSheetDialogFragment() {

    private var _binding : FragmentNotificationInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentNotificationInformationBinding.inflate(inflater, container, false)
        val root : View = binding.root
        val selectedOperation = viewModel.allPlannedOperations.value!![viewModel.selectedPlannedOperationIndex]
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
                    val notificationManager = NotificationCancelManager(requireContext())
                    notificationManager.cancelNotification(selectedOperation.code)
                    viewModel.deletePlannedOperation(selectedOperation)
                    dismiss()
                }
                .setNegativeButton("NO") {dialog, which ->
                }
                .show()
        }

        binding.note.text = selectedOperation.note

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}