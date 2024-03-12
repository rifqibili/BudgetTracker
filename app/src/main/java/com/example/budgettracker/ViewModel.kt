package com.example.budgettracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.accounts.AccountsData
import com.example.budgettracker.operations.OperationsData
import com.example.budgettracker.operations.expense.AddData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application){


    private val dataBase = AppDataBase.getDatabase(application)
    private val operationsDao = dataBase.operationDao()
    private val accountsDao = dataBase.accountDao()
    val operationsList : LiveData<List<OperationsData>> = operationsDao.getAllOperations()
    val allAccounts : LiveData<List<AccountsData>> = accountsDao.getAllAccounts()
    var totalSum = MutableLiveData<Int>()
    val paymentAccounts = arrayListOf<AccountsData>()
    val savingsAccounts = arrayListOf<AccountsData>()
    val allExpenses = arrayListOf<OperationsData>()
    val allIncomes = arrayListOf<OperationsData>()
    var listForInformation = listOf<OperationsData>()
    var selectedAccountIndex = 0
    var isSavingsSelected = false
    var selectedOperationIndex = 0
    lateinit var operationForChange : OperationsData
    lateinit var accountForChange : AccountsData
    var analyzedCategoryIndexIncome = 0 // index of category chosen from RV with categories for certain month (incomes pie)
    var analyzedCategoryIndexExpense = 0 // index of category chosen from RV with categories for certain month (expenses pie)
    var analyzedCategoriesListExpense = ArrayList<OperationsData>() // list with all categories for certain month (for expense pie)
    var analyzedCategoriesListIncome = ArrayList<OperationsData>() // list with all categories for certain month (for income pie)
    var analyzedMonthIndexIncome = 0 // for pie. index shows what months from list with operations by month should be analyzed (income)
    var analyzedMonthIndexExpense = 0 // for pie. index shows what months from list with operations by month should be analyzed (expense)
    var analyzedMonthIndexForBar = 0  // for bar. index shows what months from list with operations by month should be analyzed
    var analyzedOperationsListExpense = ArrayList<ArrayList<OperationsData>>() // all expenses divided by month
    var analyzedOperationsListIncome = ArrayList<ArrayList<OperationsData>>() // all incomes divided by month
    var lastExpenseMonthIndex = 0 // index shows what month should be displayed in expense pie
    var lastIncomeMonthIndex = 0 // index shows what month should be displayed in income pie
    var selectedYear = 0 // to divide same months from different years in analyze when touches bar diagram
    var typeOfAnalyzedOperation = 0 // Expense or Income for analyze when touches bar on bar diagram

    fun deleteAccount(account: AccountsData) {
        viewModelScope.launch(Dispatchers.IO) {
            accountsDao.deleteAccount(account)
        }
    }

    fun changeOperationAccount(newAccountName : String, oldAccountName : String) {
        for (element in operationsList.value!!) {
            if (element.account == oldAccountName){
                element.account = newAccountName
                updateOperation(element)
            }
        }
    }
    fun deleteAccountOperations(account : AccountsData) {
        viewModelScope.launch(Dispatchers.IO) {
            for (element in operationsList.value!!) {
                if (element.account == account.name) {
                    operationsDao.deleteOperation(element)
                }
            }
        }
    }
    fun deleteOperation(operation : OperationsData) {
        viewModelScope.launch(Dispatchers.IO) {
            operationsDao.deleteOperation(operation)
            operation.isForDelete = true
            updateAccountBalance(operation)
        }
    }

    fun undoDelete(operation : OperationsData) {
        viewModelScope.launch(Dispatchers.IO) {
            operationsDao.insertOperation(operation)
            updateAccountBalance(operation)
        }
    }

    fun updateOperation(operation: OperationsData) {
        viewModelScope.launch(Dispatchers.IO) {
            operationsDao.updateOperation(operation)
        }
    }

    fun divideAccounts(){
        val allAccounts = allAccounts.value
        savingsAccounts.clear()
        paymentAccounts.clear()
        for (i in 0 until allAccounts!!.size) {
            if (allAccounts[i].isSavings) {
                savingsAccounts.add(allAccounts[i])
            } else
                paymentAccounts.add(allAccounts[i])
        }
    }

    fun divideExpenses(list : List<OperationsData>) {
        allExpenses.clear()
        for (element in list) {
            if (element.type == "Expense") {
                allExpenses.add(element)
            }
        }
    }

    fun divideIncomes(list : List<OperationsData>) {
        allIncomes.clear()
        for (element in list) {
            if (element.type == "Income") {
                allIncomes.add(element)
            }
        }
    }

    fun collectByCategory(list : List<OperationsData>) : ArrayList<OperationsData> {
        val result = arrayListOf<OperationsData>()
        val blackList = arrayListOf<String>()
        for (i in 0 until list.size) {
            var totalAmount = 0
            var operationsCount = 0
            if (list[i].category !in blackList) {
                for (j in 0 until list.size) {
                    if (list[i].category == list[j].category) {
                        totalAmount += list[j].amount.toInt()
                        operationsCount++
                    }
                }
                blackList.add(list[i].category)
                result.add(OperationsData(0, totalAmount.toString(), list[i].icon, list[i].category,
                    "Transfer", list[i].date , "Operations: $operationsCount", "", false, list[i].color, list[i].note))
            }
        }
        return result
    }

    fun isUniqueAccountName(name : String) : Boolean {
        for (element in allAccounts.value!!) {
            if (element.name == name)
                return false
        }
        return true
    }

    fun divideOperationsByMonth(list : List<OperationsData>) : ArrayList<ArrayList<OperationsData>> {
        val result : ArrayList<ArrayList<OperationsData>> = arrayListOf()
        if (list.isEmpty()) {
            return result
        }
        result.add(arrayListOf())
        result[0].add(list[0])
        var j = 0
        for (i in 1 until list.size) {
            if (list[i].date.month == list[i - 1].date.month && list[i].date.year == list[i - 1].date.year) {
                result[j].add(list[i])
            }
            else {
                result.add(arrayListOf())
                j++
                result[j].add(list[i])
            }
        }
        return result
    }

    fun total() {
        viewModelScope.launch(Dispatchers.Main) {
            totalSum.value = calculateTotal()
            Log.d("totaling", "Total updated: ${totalSum.value}")
        }
    }

    private fun calculateTotal(): Int {
        var sum = 0
        for (i in 0 until (allAccounts.value?.size ?: 0)) {
            sum += allAccounts.value!![i].balance.toInt()
        }
        return sum
    }


    fun updateAccountBalance(operation: OperationsData) {
        viewModelScope.launch(Dispatchers.IO) {
            val accountsList = allAccounts.value.orEmpty().toMutableList()
            for (i in 0 until accountsList.size) {
                if (accountsList[i].name == operation.account) {
                    val amountMultiplier = if (!operation.isForDelete) 1 else -1

                    when (operation.type) {
                        "Income" -> updateBalance(accountsList[i], amountMultiplier * operation.amount.toInt())
                        "Expense" -> updateBalance(accountsList[i], -amountMultiplier * operation.amount.toInt())
                        "Transfer" -> {
                            val transferTo = accountsList.first { it.name == operation.transferTo }
                            updateBalance(accountsList[i], -amountMultiplier * operation.amount.toInt())
                            updateBalance(transferTo, amountMultiplier * operation.amount.toInt())
                        }
                    }
                }
            }
        }
    }

    fun updateBalance(account: AccountsData, amount: Int) {
        account.balance = (account.balance.toInt() + amount).toString()
        accountsDao.updateAccount(account)
    }

    fun addAccount(account : AccountsData){
        viewModelScope.launch(Dispatchers.IO) {
            accountsDao.insertAccount(account)
        }
    }

    fun addOperation(operation : OperationsData){
        viewModelScope.launch(Dispatchers.IO) {
            operationsDao.insertOperation(operation)
            updateAccountBalance(operation)
            total()
        }
    }

    var expenseList = MutableLiveData<MutableList<AddData>>()
    fun addExpense(expense : AddData){
        val currentList = expenseList.value ?: mutableListOf()
        currentList.add(expense)
        expenseList.value = currentList
    }
    fun clearExpense(){
        expenseList.value?.clear()
    }
    var incomeList = MutableLiveData<MutableList<AddData>>()
    fun addIncome(income : AddData){
        val currentList = incomeList.value ?: mutableListOf()
        currentList.add(income)
        incomeList.value = currentList
    }
    fun clearIncome(){
        incomeList.value?.clear()
    }


    fun getOperationsFromDatabase(): LiveData<List<OperationsData>> {
        return operationsDao.getAllOperations()
    }

    fun getAccountsFromDatabase(): LiveData<List<AccountsData>> {
        return accountsDao.getAllAccounts()
    }



}