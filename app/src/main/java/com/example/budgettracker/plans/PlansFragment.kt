package com.example.budgettracker.plans

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.budgettracker.R
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.databinding.FragmentPlansBinding
import com.example.budgettracker.operations.expense.AddData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlansFragment : Fragment() {

    private var _binding : FragmentPlansBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseCategoriesList: MutableList<AddData>
    private lateinit var allExpenseCategoriesList: List<AddData>
    private lateinit var allIncomeCategoriesList: List<AddData>
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createNotificationChannel()
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentPlansBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.VISIBLE
        viewModel.lastExpenseMonthIndex = 0
        viewModel.lastIncomeMonthIndex = 0

        viewModel.divideExpenses(viewModel.operationsList.value!!)

        val expense = context?.assets
            ?.open("expense_categories.json")
            ?.bufferedReader()
            .use { it!!.readText() }
        val gson = GsonBuilder().create()
        allExpenseCategoriesList = gson.fromJson(expense,Array<AddData>::class.java).toList()
        expenseCategoriesList = gson.fromJson(expense,Array<AddData>::class.java).toMutableList()
        val income = context?.assets
            ?.open("income_categories.json")
            ?.bufferedReader()
            .use { it!!.readText() }

        allIncomeCategoriesList = gson.fromJson(income,Array<AddData>::class.java).toList()

        viewModel.allLimits.observe(viewLifecycleOwner, Observer{
            for (element in viewModel.allLimits.value!!) {
                for (i in 0 until expenseCategoriesList.size) {
                    if (element.categoryName == expenseCategoriesList[i].categoryName) {
                        expenseCategoriesList.removeAt(i)
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
        val adapter = CategoriesSpinnerAdapter(requireContext(), expenseCategoriesList)
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
            var adapterRV = LimitsAdapter(viewModel.allLimits.value!!, findNavController(), viewModel)
            adapterRV.setOnItemLongClickListener(object : LimitsAdapter.OnItemLongClickListener {
                override fun onItemLongClick(position: Int) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete selected limit?")
                        .setPositiveButton("YES") { dialog, which ->
                            viewModel.deleteLimit(viewModel.allLimits.value!![position])
                        }
                        .setNegativeButton("NO") {dialog, which ->
                        }
                        .show()
                }
            })

            binding.limitsRV.adapter = adapterRV

        })


        /*
            val intent = Intent(requireContext(), ReminderBroadcast::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                111,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val currentTime = System.currentTimeMillis() + 3000
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime, pendingIntent)
         */

        val dialogNotification = Dialog(requireContext())
        dialogNotification.setContentView(R.layout.notification_alert_dialog)
        dialogNotification.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogNotification.setCanceledOnTouchOutside(false)

        binding.addNotification.setOnClickListener {
            dialogNotification.show()
        }

        val cancelNotificationDialog : Button = dialogNotification.findViewById(R.id.cancel)
        cancelNotificationDialog.setOnClickListener {
            dialogNotification.cancel()
        }
        val notificationSpinner = dialogNotification.findViewById<MaterialAutoCompleteTextView>(R.id.notificationCategorySelect)
        val notificationSpinnerField = dialogNotification.findViewById<TextInputLayout>(R.id.notificationCategorySelectField)
        var notificationAdapter = CategoriesSpinnerAdapter(requireContext(), expenseCategoriesList)
        notificationSpinner.setAdapter(notificationAdapter)

        val expenseButton : RadioButton = dialogNotification.findViewById(R.id.expenseRadioButton)
        val incomeButton : RadioButton = dialogNotification.findViewById(R.id.incomeRadioButton)
        expenseButton.isChecked = true
        expenseButton.setTextColor(Color.WHITE)
        val radioGroup : RadioGroup = dialogNotification.findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.expenseRadioButton -> {
                    notificationSpinner.setText("", false)
                    expenseButton.isChecked = true
                    expenseButton.setTextColor(Color.WHITE)
                    incomeButton.setTextColor(Color.BLACK)
                    notificationAdapter = CategoriesSpinnerAdapter(requireContext(), allExpenseCategoriesList)
                    notificationSpinner.setAdapter(notificationAdapter)
                }
                R.id.incomeRadioButton -> {
                    notificationSpinner.setText("", false)
                    incomeButton.isChecked = true
                    expenseButton.setTextColor(Color.BLACK)
                    incomeButton.setTextColor(Color.WHITE)
                    notificationAdapter = CategoriesSpinnerAdapter(requireContext(), allIncomeCategoriesList)
                    notificationSpinner.setAdapter(notificationAdapter)
                }
            }
        }

        val selectedNotificationAddData = AddData("", "", true)
        notificationSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedNotificationAddData.categoryIcon = notificationAdapter.getItem(position)!!.categoryIcon
            selectedNotificationAddData.categoryName = notificationAdapter.getItem(position)!!.categoryName
            notificationSpinner.setText(selectedNotificationAddData.categoryName, false)
        }

        val accounts = ArrayList<String>()
        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            for (element in it){
                accounts.add(element.name)
            }
        })

        val accountsSpinner = dialogNotification.findViewById<MaterialAutoCompleteTextView>(R.id.accountSelect)
        val accountsSpinnerField = dialogNotification.findViewById<TextInputLayout>(R.id.accountSelectField)
        val accountsAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, accounts)
        accountsSpinner.setAdapter(accountsAdapter)
        var selectedAccount = ""
        accountsSpinner.setOnItemClickListener { parent, view, position, id ->
            selectedAccount = accounts[position]
        }

        val notificationAmount : EditText = dialogNotification.findViewById(R.id.amount)
        var amountValue = "0"
        notificationAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()){
                    amountValue = s.toString()
                }
                else { amountValue = "0" }
            }

        })

        val note : EditText = dialogNotification.findViewById(R.id.note)
        var noteText = ""
        note.addTextChangedListener(
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

        val timePicker : Button = dialogNotification.findViewById(R.id.timePicker)
        val datePicker : Button = dialogNotification.findViewById(R.id.datePicker)
        val today = Calendar.getInstance()
        val selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH mm", Locale.getDefault())
        datePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate.set(year, monthOfYear, dayOfMonth)
                datePicker.setText(dateFormat.format(selectedDate.time))
            },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.show()
        }
        timePicker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(requireContext(), {TimePicker, hourOfDay : Int, minute : Int ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDate.set(Calendar.MINUTE, minute)
                timePicker.setText(timeFormat.format(selectedDate.time))
            },
                today.get(Calendar.HOUR_OF_DAY),
                today.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }


        val addNotification : Button = dialogNotification.findViewById(R.id.addNotification)
        addNotification.setOnClickListener {
            if (selectedAccount == "") {
                accountsSpinnerField.error = "Select a valid account"
            }
            if (amountValue == "0") {
                amountField.error = "Enter a valid amount"
            }
            if (datePicker.text == "Date" || timePicker.text == "Time") {
                Toast.makeText(context, "Select date and time", Toast.LENGTH_LONG).show()
            }
            if (selectedNotificationAddData.categoryName == "") {

            }

            else {

                    val intent = Intent(requireContext(), ReminderBroadcast::class.java)
                    val code = System.currentTimeMillis().toInt()
                    intent.putExtra("notificationContent", "$amountValue, ${selectedNotificationAddData.categoryName}, $selectedAccount")
                    intent.putExtra("code", code)
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        code,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    //val currentTime = System.currentTimeMillis() + 2000
                    val notificationTime = selectedDate.timeInMillis
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
                    var type = ""

                    when (expenseButton.isChecked) {
                        true -> type = "Expense"
                        false -> type = "Income"
                    }

                    val plannedOperation =
                        PlannedOperation(0, amountValue,
                            requireContext().resources.getIdentifier(selectedNotificationAddData.categoryIcon, "drawable", context?.packageName),
                            selectedNotificationAddData.categoryName, type, selectedDate.time, selectedAccount, noteText, code)
                    viewModel.addPlannedOperation(plannedOperation)
                    selectedAccount = ""
                    amountValue = "0"
                    notificationAmount.setText("")
                    expenseButton.isChecked = true
                    notificationSpinner.setText("", false)
                    accountsSpinner.setText("", false)
                    datePicker.text = "Date"
                    timePicker.text = "Time"
                    note.setText("")
                    noteText = ""
                    Log.d("TAG", "time to cancel")

                    dialogNotification.cancel()
                }
        }

        binding.notificationsRV.layoutManager = LinearLayoutManager(context)
        viewModel.allPlannedOperations.observe(viewLifecycleOwner, Observer {
            /*
            var adapterRV = NotificationsAdapter(it, findNavController(), viewModel)
            adapterRV.setOnItemLongClickListener(object : NotificationsAdapter.OnItemLongClickListener {
                override fun onItemLongClick(position: Int) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete selected notification?")
                        .setPositiveButton("YES") { dialog, which ->
                            viewModel.deletePlannedOperation(it[position])
                        }
                        .setNegativeButton("NO") {dialog, which ->
                        }
                        .show()
                }
            })

            binding.notificationsRV.adapter = adapterRV

             */
            binding.notificationsRV.adapter = NotificationsAdapter(it, findNavController(), viewModel)
        })

        return root
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel"
            val descriptionText = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("mama", name, importance)
            channel.description = descriptionText
            notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}