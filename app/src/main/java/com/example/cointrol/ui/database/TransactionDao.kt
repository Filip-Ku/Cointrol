package com.example.cointrol.ui.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    @Query("SELECT * FROM transactions WHERE type=\"income\" ORDER BY date  LIMIT 3 ")
    suspend fun getLastThreeIncomes(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE type=\"outcome\" ORDER BY date  LIMIT 3 ")
    suspend fun getLastThreeOutcomes(): List<Transaction>

}