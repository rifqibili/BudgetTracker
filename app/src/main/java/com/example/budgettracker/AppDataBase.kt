package com.example.budgettracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgettracker.accounts.AccountsDao
import com.example.budgettracker.accounts.AccountsData
import com.example.budgettracker.operations.OperationsDao
import com.example.budgettracker.operations.OperationsData

@Database(entities = [OperationsData::class, AccountsData::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase(){
    abstract fun operationDao() : OperationsDao
    abstract fun accountDao() : AccountsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}