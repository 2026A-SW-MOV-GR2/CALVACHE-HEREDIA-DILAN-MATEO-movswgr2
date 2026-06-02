package com.example.econome.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RoomTransaction): Long

    @androidx.room.Update
    suspend fun updateTransaction(transaction: RoomTransaction)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: RoomTransaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<RoomTransaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getTransactionsList(): List<RoomTransaction>
}
