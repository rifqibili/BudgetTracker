package com.example.budgettracker.accounts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountsDao {
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): LiveData<List<AccountsData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: AccountsData)

    @Update
    fun updateAccount(account: AccountsData)

    @Delete
    fun deleteAccount(account: AccountsData)
}
