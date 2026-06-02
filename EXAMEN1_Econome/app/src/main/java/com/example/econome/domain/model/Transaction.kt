package com.example.econome.domain.model

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String = "",
    val concept: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Otros"
)
