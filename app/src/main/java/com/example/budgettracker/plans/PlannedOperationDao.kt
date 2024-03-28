package com.example.budgettracker.plans

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlannedOperationDao {
    @Query("SELECT * FROM plans ORDER BY date DESC")
    fun getAllPlannedOperations(): LiveData<List<PlannedOperation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlannedOperation(plannedOperation: PlannedOperation)

    @Delete
    fun deletePlannedOperation(plannedOperation: PlannedOperation)
}