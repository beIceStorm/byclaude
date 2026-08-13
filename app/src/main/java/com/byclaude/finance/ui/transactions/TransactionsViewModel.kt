package com.byclaude.finance.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byclaude.finance.data.SettingsRepository
import com.byclaude.finance.data.Transaction
import com.byclaude.finance.data.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TransactionsUiState(
    val groupedTransactions: List<Pair<LocalDate, List<Transaction>>> = emptyList(),
    val currencySymbol: String = "₽",
    val isLoading: Boolean = true
)

class TransactionsViewModel(
    private val repository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<TransactionsUiState> = combine(
        repository.allTransactions,
        settingsRepository.currencySymbol
    ) { transactions, symbol ->
        val grouped = transactions
            .groupBy { LocalDate.ofEpochDay(it.dateEpochDay) }
            .toList()
        TransactionsUiState(grouped, symbol, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsUiState()
    )

    fun delete(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }
}
