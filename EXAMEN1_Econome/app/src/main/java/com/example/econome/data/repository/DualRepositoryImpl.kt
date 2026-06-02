package com.example.econome.data.repository

import android.util.Log
import com.example.econome.data.datasource.NoSqlDataSource
import com.example.econome.data.local.TransactionDao
import com.example.econome.data.local.toDomain
import com.example.econome.data.local.toRoom
import com.example.econome.domain.model.Transaction
import com.example.econome.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DualRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val noSqlDataSource: NoSqlDataSource
) : TransactionRepository {

    private val TAG = "EconoMe_Persistence"

    private val _isSqlActive = MutableStateFlow(true)
    val isSqlActive: StateFlow<Boolean> = _isSqlActive

    init {
        Log.i(TAG, "INFO: DualRepositoryImpl inicializado. Motor por defecto: SQL (Room).")
    }

    override fun toggleDatabaseEngine(useSql: Boolean) {
        _isSqlActive.value = useSql
        val motorActual = if (useSql) "SQL (Room)" else "Local NoSQL (JSON)"
        Log.d(TAG, "DEBUG: Motor de persistencia conmutado a -> $motorActual")
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        try {
            if (_isSqlActive.value) {
                Log.d(TAG, "DEBUG: Iniciando inserción en SQLite (Room) para TX: ${transaction.concept}")
                transactionDao.insertTransaction(transaction.toRoom())
                Log.i(TAG, "INFO: Transacción guardada exitosamente en SQL local.")
            } else {
                Log.d(TAG, "DEBUG: Iniciando escritura en Local NoSQL para TX: ${transaction.concept}")
                noSqlDataSource.save(transaction)
                Log.i(TAG, "INFO: Transacción guardada exitosamente en NoSQL local.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Fallo crítico al persistir la transacción ${transaction.concept}", e)
            throw e
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        try {
            if (_isSqlActive.value) {
                Log.d(TAG, "DEBUG: Actualizando en Room: ${transaction.id}")
                transactionDao.updateTransaction(transaction.toRoom().copy(id = transaction.id.toIntOrNull() ?: 0))
            } else {
                Log.d(TAG, "DEBUG: Actualizando en Local NoSQL: ${transaction.id}")
                noSqlDataSource.update(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Fallo al actualizar ${transaction.id}", e)
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        try {
            if (_isSqlActive.value) {
                Log.d(TAG, "DEBUG: Eliminando de Room: ${transaction.id}")
                transactionDao.deleteTransaction(transaction.toRoom().copy(id = transaction.id.toIntOrNull() ?: 0))
            } else {
                Log.d(TAG, "DEBUG: Eliminando de Local NoSQL: ${transaction.id}")
                noSqlDataSource.delete(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Fallo al eliminar ${transaction.id}", e)
        }
    }

    override suspend fun getTransactions(): List<Transaction> {
        return try {
            if (_isSqlActive.value) {
                Log.d(TAG, "DEBUG: Leyendo lista de transacciones desde SQLite (Room)")
                transactionDao.getTransactionsList().map { it.toDomain() }
            } else {
                Log.d(TAG, "DEBUG: Leyendo lista de transacciones desde Local NoSQL")
                noSqlDataSource.getAll()
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Fallo al leer las transacciones", e)
            emptyList()
        }
    }
}
