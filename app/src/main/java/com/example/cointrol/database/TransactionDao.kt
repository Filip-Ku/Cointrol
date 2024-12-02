package com.example.cointrol.database

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

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    @Query("SELECT * FROM transactions WHERE type=:type ORDER BY date  LIMIT 3 ")
    suspend fun getLastThreeTransactions(type: String): List<Transaction>

    @Query("""
    SELECT * 
    FROM transactions 
    WHERE type = :type 
    ORDER BY strftime('%Y-%m-%d', 
        substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) DESC
    """)
    suspend fun getAllTransactions(type: String): List<Transaction>

    @Query("""
    SELECT * 
    FROM transactions 
    WHERE type = :type 
    AND strftime('%Y-%m-%d', 
            substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) 
            BETWEEN strftime('%Y-%m-%d', :startDate) 
            AND strftime('%Y-%m-%d', :endDate)
            ORDER BY date DESC
    """)
    suspend fun getTransactionsForDateRange(
        type: String,
        startDate: String,
        endDate: String
    ): List<Transaction>


    @Query("SELECT MAX(amount) FROM transactions WHERE type = :type ")
    suspend fun getHighestTransaction(type: String): Double?

    @Query("SELECT MAX(amount) FROM transactions WHERE type = :type AND strftime('%Y-%m-%d', \n" +
            "            substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) \n" +
            "            BETWEEN strftime('%Y-%m-%d', :startDate) \n" +
            "            AND strftime('%Y-%m-%d', :endDate)")
    suspend fun getHighestTransactionForDateRange(type: String,startDate:String,endDate: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getCombinedTransaction(type: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type  AND strftime('%Y-%m-%d', \n" +
            "            substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) \n" +
            "            BETWEEN strftime('%Y-%m-%d', :startDate) \n" +
            "            AND strftime('%Y-%m-%d', :endDate)")
    suspend fun getCombinedTransactionForDateRange(type: String,startDate:String,endDate: String): Double?

    @Query("SELECT * FROM transactions WHERE `desc` LIKE :phrase AND type = :type ORDER BY date DESC")
    suspend fun searchByPhrase(type: String,phrase: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE `desc` LIKE :phrase AND type = :type  AND strftime('%Y-%m-%d', \n" +
            "            substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) \n" +
            "            BETWEEN strftime('%Y-%m-%d', :startDate) \n" +
            "            AND strftime('%Y-%m-%d', :endDate)ORDER BY date DESC")
    suspend fun searchByPhraseForDateRange(type: String,phrase: String,startDate:String,endDate:String): List<Transaction>

}