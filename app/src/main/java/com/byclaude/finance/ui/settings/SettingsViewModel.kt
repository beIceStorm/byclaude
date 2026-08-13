package com.byclaude.finance.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byclaude.finance.data.SettingsRepository
import com.byclaude.finance.data.ThemeMode
import com.byclaude.finance.data.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currencySymbol: String = "₽",
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.currencySymbol,
        settingsRepository.themeMode
    ) { symbol, mode -> SettingsUiState(symbol, mode) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setCurrency(symbol: String) {
        viewModelScope.launch { settingsRepository.setCurrency(symbol) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun clearAllData() {
        viewModelScope.launch { transactionRepository.clearAll() }
    }
}
