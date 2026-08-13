package com.byclaude.finance.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byclaude.finance.data.SettingsRepository
import com.byclaude.finance.data.Transaction
import com.byclaude.finance.data.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val currencySymbol: String = "₽",
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.allTransactions,
        settingsRepository.currencySymbol
    ) { transactions, symbol ->
        val currentMonth = YearMonth.now()
        val monthTransactions = transactions.filter {
            YearMonth.from(LocalDate.ofEpochDay(it.dateEpochDay)) == currentMonth
        }
        HomeUiState(
            totalBalance = transactions.sumOf { it.signedAmount },
            monthIncome = monthTransactions.filter { it.isIncome }.sumOf { it.amount },
            monthExpense = monthTransactions.filter { !it.isIncome }.sumOf { it.amount },
            recentTransactions = transactions.take(8),
            currencySymbol = symbol,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
