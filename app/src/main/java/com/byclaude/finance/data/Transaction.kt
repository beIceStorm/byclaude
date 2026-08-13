package com.byclaude.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: Category,
    val note: String,
    val dateEpochDay: Long,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
) {
    val isIncome: Boolean get() = category.type == CategoryType.INCOME
    val signedAmount: Double get() = if (isIncome) amount else -amount
}
