package com.example.econome.data.datasource

import android.content.Context
import com.example.econome.domain.model.Transaction
import com.example.econome.domain.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalFileNoSqlDataSource(private val context: Context) : NoSqlDataSource {
    private val fileName = "nosql_transactions.json"
    private val gson = Gson()
    private val file = File(context.filesDir, fileName)

    private suspend fun readFromFile(): MutableList<Transaction> = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext mutableListOf<Transaction>()
        try {
            val json = file.readText()
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private suspend fun writeToFile(transactions: List<Transaction>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(transactions)
        file.writeText(json)
    }

    override suspend fun save(transaction: Transaction) {
        val current = readFromFile()
        current.add(transaction)
        writeToFile(current)
    }

    override suspend fun update(transaction: Transaction) {
        val current = readFromFile()
        val index = current.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            current[index] = transaction
            writeToFile(current)
        }
    }

    override suspend fun delete(transaction: Transaction) {
        val current = readFromFile()
        current.removeAll { it.id == transaction.id }
        writeToFile(current)
    }

    override suspend fun getAll(): List<Transaction> {
        return readFromFile()
    }
}
