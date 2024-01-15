package com.example.budgettracker.operations
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "operations")
data class OperationsData(
    @PrimaryKey(autoGenerate = true) var id : Int,
    val amount: String,
    val icon : Int,
    val category : String,
    val type : String,
    val date : Date,
    val account : String,
    val transferTo : String,
    var isForDelete : Boolean,
    var color : Int)
