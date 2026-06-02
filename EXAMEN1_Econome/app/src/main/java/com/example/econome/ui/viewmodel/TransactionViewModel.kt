package com.example.econome.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.econome.domain.model.Transaction
import com.example.econome.domain.model.TransactionType
import com.example.econome.domain.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // UI Navigation & State
    var currentTab by mutableStateOf(0) // 0: Transactions, 1: Dashboard
    var isSqlActive by mutableStateOf(true)
    var showAddDialog by mutableStateOf(false)
    var editingTransaction by mutableStateOf<Transaction?>(null)

    // Form State
    var concept by mutableStateOf("")
    var amount by mutableStateOf("")
    var selectedType by mutableStateOf(TransactionType.EXPENSE)
    var selectedCategory by mutableStateOf("Otros")
    var selectedDate by mutableStateOf(System.currentTimeMillis())

    // Data State
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    // Dashboard Derived State
    val totalIncome by derivedStateOf {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalExpenses by derivedStateOf {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val balance by derivedStateOf { totalIncome - totalExpenses }

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            transactions = repository.getTransactions()
        }
    }

    fun onToggleEngine(useSql: Boolean) {
        isSqlActive = useSql
        repository.toggleDatabaseEngine(useSql)
        loadTransactions()
    }

    fun startEditing(transaction: Transaction) {
        editingTransaction = transaction
        concept = transaction.concept
        amount = transaction.amount.toString()
        selectedType = transaction.type
        selectedCategory = transaction.category
        selectedDate = transaction.date
        showAddDialog = true
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            loadTransactions()
        }
    }

    fun save() {
        android.util.Log.e("PRUEBA", "1. Botón de guardar presionado")
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        
        if (concept.isNotBlank() && amountValue > 0) {
            android.util.Log.e("PRUEBA", "2. Validación pasada. Guardando TX: $concept - $amountValue")
            val transaction = Transaction(
                id = editingTransaction?.id ?: UUID.randomUUID().toString(),
                concept = concept,
                amount = amountValue,
                date = selectedDate,
                type = selectedType,
                category = selectedCategory
            )
            viewModelScope.launch {
                android.util.Log.e("PRUEBA", "3. Llamando al repositorio...")
                try {
                    if (editingTransaction == null) {
                        repository.saveTransaction(transaction)
                    } else {
                        repository.updateTransaction(transaction)
                    }
                    android.util.Log.e("PRUEBA", "4. Repositorio terminó. Limpiando campos...")
                    resetForm()
                    showAddDialog = false
                    loadTransactions()
                } catch (e: Exception) {
                    android.util.Log.e("PRUEBA", "ERROR en ViewModel al guardar: ${e.message}", e)
                }
            }
        } else {
             android.util.Log.e("PRUEBA", "X. Validación falló: El concepto está vacío o el monto es 0")
        }
    }

    fun resetForm() {
        editingTransaction = null
        concept = ""
        amount = ""
        selectedType = TransactionType.EXPENSE
        selectedCategory = "Otros"
        selectedDate = System.currentTimeMillis()
    }
}
