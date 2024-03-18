package com.example.budgettracker.plans

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LimitsDao {
    @Query("SELECT * FROM limits")
    fun getAllLimits(): LiveData<List<LimitsData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLimit(limit: LimitsData)

    @Delete
    fun deleteLimit(limit: LimitsData)

}