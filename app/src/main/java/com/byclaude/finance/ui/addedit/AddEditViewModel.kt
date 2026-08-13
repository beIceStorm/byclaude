package com.byclaude.finance.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byclaude.finance.data.Category
import com.byclaude.finance.data.CategoryType
import com.byclaude.finance.data.SettingsRepository
import com.byclaude.finance.data.Transaction
import com.byclaude.finance.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddEditUiState(
    val id: Long? = null,
    val amountText: String = "",
    val isIncome: Boolean = false,
    val category: Category = Category.FOOD,
    val note: String = "",
    val dateEpochDay: Long = LocalDate.now().toEpochDay(),
    val currencySymbol: String = "₽",
    val isEditing: Boolean = false,
    val showAmountError: Boolean = false,
    val saved: Boolean = false
)

class AddEditViewModel(
    private val repository: TransactionRepository,
    settingsRepository: SettingsRepository,
    transactionId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.currencySymbol.collect { symbol ->
                _uiState.update { it.copy(currencySymbol = symbol) }
            }
        }
        if (transactionId != null) {
            viewModelScope.launch {
                repository.getById(transactionId)?.let { transaction ->
                    _uiState.update {
                        it.copy(
                            id = transaction.id,
                            amountText = formatAmountForInput(transaction.amount),
                            isIncome = transaction.isIncome,
                            category = transaction.category,
                            note = transaction.note,
                            dateEpochDay = transaction.dateEpochDay,
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun onAmountChange(text: String) {
        _uiState.update { it.copy(amountText = sanitizeAmountInput(text), showAmountError = false) }
    }

    fun onTypeChange(isIncome: Boolean) {
        _uiState.update { current ->
            val targetType = if (isIncome) CategoryType.INCOME else CategoryType.EXPENSE
            val category = if (current.category.type == targetType) current.category
            else if (isIncome) Category.SALARY else Category.FOOD
            current.copy(isIncome = isIncome, category = category)
        }
    }

    fun onCategoryChange(category: Category) {
        _uiState.update { it.copy(category = category, isIncome = category.type == CategoryType.INCOME) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onDateChange(epochDay: Long) {
        _uiState.update { it.copy(dateEpochDay = epochDay) }
    }

    fun save() {
        val amount = _uiState.value.amountText.replace(',', '.').toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(showAmountError = true) }
            return
        }
        viewModelScope.launch {
            val state = _uiState.value
            val transaction = Transaction(
                id = state.id ?: 0,
                amount = amount,
                category = state.category,
                note = state.note.trim(),
                dateEpochDay = state.dateEpochDay
            )
            if (state.isEditing) repository.update(transaction) else repository.add(transaction)
            _uiState.update { it.copy(saved = true) }
        }
    }

    fun deleteTransaction() {
        val id = _uiState.value.id ?: return
        viewModelScope.launch {
            repository.getById(id)?.let { repository.delete(it) }
            _uiState.update { it.copy(saved = true) }
        }
    }
}

private fun formatAmountForInput(amount: Double): String {
    val rounded = Math.round(amount * 100) / 100.0
    return if (rounded == Math.floor(rounded)) rounded.toLong().toString()
    else rounded.toString().replace('.', ',')
}

private fun sanitizeAmountInput(raw: String): String {
    var hasSeparator = false
    val builder = StringBuilder()
    for (character in raw) {
        when {
            character.isDigit() -> builder.append(character)
            (character == '.' || character == ',') && !hasSeparator -> {
                builder.append(',')
                hasSeparator = true
            }
        }
    }
    val text = builder.toString()
    val separatorIndex = text.indexOf(',')
    return if (separatorIndex == -1) text else {
        val decimals = text.substring(separatorIndex + 1).take(2)
        text.substring(0, separatorIndex + 1) + decimals
    }
}
