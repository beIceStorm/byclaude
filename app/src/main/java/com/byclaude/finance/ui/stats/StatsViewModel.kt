package com.byclaude.finance.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byclaude.finance.data.Category
import com.byclaude.finance.data.SettingsRepository
import com.byclaude.finance.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth

data class CategorySlice(
    val category: Category,
    val amount: Double,
    val fraction: Float
)

data class StatsUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val slices: List<CategorySlice> = emptyList(),
    val totalExpense: Double = 0.0,
    val currencySymbol: String = "₽",
    val isLoading: Boolean = true
)

class StatsViewModel(
    private val repository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<StatsUiState> = combine(
        repository.allTransactions,
        settingsRepository.currencySymbol,
        selectedMonth
    ) { transactions, symbol, month ->
        val monthExpenses = transactions.filter {
            !it.isIncome && YearMonth.from(LocalDate.ofEpochDay(it.dateEpochDay)) == month
        }
        val total = monthExpenses.sumOf { it.amount }
        val slices = monthExpenses
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
            .map { (category, amount) ->
                CategorySlice(category, amount, if (total > 0) (amount / total).toFloat() else 0f)
            }
        StatsUiState(month, slices, total, symbol, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    fun previousMonth() {
        selectedMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        selectedMonth.update { it.plusMonths(1) }
    }
}
