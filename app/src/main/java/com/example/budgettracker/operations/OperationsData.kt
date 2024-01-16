package com.example.budgettracker.operations
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "operations")
data class OperationsData(
    @PrimaryKey(autoGenerate = true) var id : Int,
    var amount: String,
    var icon : Int,
    var category : String,
    var type : String,
    var date : Date,
    var account : String,
    val transferTo : String,
    var isForDelete : Boolean,
    var color : Int)
