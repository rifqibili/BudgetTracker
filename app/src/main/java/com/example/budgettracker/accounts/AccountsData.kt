package com.example.budgettracker.accounts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountsData(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val name : String,
    var balance : String,
    val accountType : String,
    val isSavings : Boolean)
