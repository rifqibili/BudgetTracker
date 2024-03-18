package com.example.budgettracker.plans

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "limits")
data class LimitsData(
    @PrimaryKey(autoGenerate = true) var id : Int,
    var value : Double,
    var categoryIcon : String,
    var categoryName : String
)
