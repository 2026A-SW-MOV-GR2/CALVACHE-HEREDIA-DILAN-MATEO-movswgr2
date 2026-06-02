package com.example.econome.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.econome.domain.model.Transaction
import com.example.econome.domain.model.TransactionType

@Entity(tableName = "transactions")
data class RoomTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val concept: String,
    val amount: Double,
    val date: Long,
    val type: String,
    val category: String
)

fun Transaction.toRoom() = RoomTransaction(
    concept = concept,
    amount = amount,
    date = date,
    type = type.name,
    category = category
)

fun RoomTransaction.toDomain() = Transaction(
    id = id.toString(),
    concept = concept,
    amount = amount,
    date = date,
    type = TransactionType.valueOf(type),
    category = category
)
