package com.byclaude.finance.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = dao.getAll()

    suspend fun getById(id: Long): Transaction? = dao.getById(id)

    suspend fun add(transaction: Transaction): Long = dao.insert(transaction)

    suspend fun update(transaction: Transaction) = dao.update(transaction)

    suspend fun delete(transaction: Transaction) = dao.delete(transaction)

    suspend fun clearAll() = dao.clearAll()
}
