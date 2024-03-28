package com.example.budgettracker.plans

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "plans")
data class PlannedOperation(
    @PrimaryKey(autoGenerate = true) var id : Int,
    var amount: String,
    var icon : Int,
    var category : String,
    var type : String,
    var date : Date,
    var account : String,
    var note : String,
    val code : Int
)
