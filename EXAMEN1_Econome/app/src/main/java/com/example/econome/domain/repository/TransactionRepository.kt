package com.example.econome.domain.repository

import com.example.econome.domain.model.Transaction

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun getTransactions(): List<Transaction>
    fun toggleDatabaseEngine(useSql: Boolean)
}
