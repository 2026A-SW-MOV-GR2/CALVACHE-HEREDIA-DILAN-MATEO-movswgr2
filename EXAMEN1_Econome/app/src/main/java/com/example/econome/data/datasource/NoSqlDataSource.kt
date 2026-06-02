package com.example.econome.data.datasource

import com.example.econome.domain.model.Transaction

interface NoSqlDataSource {
    suspend fun save(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun getAll(): List<Transaction>
}
