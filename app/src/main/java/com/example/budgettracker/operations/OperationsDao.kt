package com.example.budgettracker.operations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OperationsDao {
    @Query("SELECT * FROM operations ORDER BY date DESC")
    fun getAllOperations(): LiveData<List<OperationsData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperation(operation: OperationsData)

    @Update
    fun updateOperation(operation: OperationsData)

    @Delete
    fun deleteOperation(operation : OperationsData)

}