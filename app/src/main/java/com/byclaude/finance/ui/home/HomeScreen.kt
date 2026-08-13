package com.byclaude.finance.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byclaude.finance.R
import com.byclaude.finance.data.ServiceLocator
import com.byclaude.finance.ui.SimpleViewModelFactory
import com.byclaude.finance.ui.components.EmptyState
import com.byclaude.finance.ui.components.TransactionRow
import com.byclaude.finance.ui.theme.ExpenseRed
import com.byclaude.finance.ui.theme.IncomeGreen
import com.byclaude.finance.util.CurrencyFormatter

@Composable
fun HomeScreen(
    onSeeAll: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = SimpleViewModelFactory {
            HomeViewModel(ServiceLocator.transactionRepository, ServiceLocator.settingsRepository)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isLoading && uiState.recentTransactions.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BalanceCard(uiState.totalBalance, uiState.currencySymbol)
            Spacer(modifier = Modifier.height(8.dp))
            MonthSummaryRow(uiState.monthIncome, uiState.monthExpense, uiState.currencySymbol)
            EmptyState(
                title = stringResource(R.string.empty_transactions_title),
                subtitle = stringResource(R.string.empty_transactions_subtitle)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item { BalanceCard(uiState.totalBalance, uiState.currencySymbol) }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            MonthSummaryRow(uiState.monthIncome, uiState.monthExpense, uiState.currencySymbol)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.recent_transactions),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onSeeAll) {
                    Text(stringResource(R.string.see_all))
                }
            }
        }
        items(uiState.recentTransactions, key = { it.id }) { transaction ->
            TransactionRow(
                transaction = transaction,
                currencySymbol = uiState.currencySymbol,
                onClick = { onTransactionClick(transaction.id) }
            )
            HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
        }
    }
}

@Composable
private fun BalanceCard(totalBalance: Double, currencySymbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.balance_total),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyFormatter.format(totalBalance, currencySymbol),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MonthSummaryRow(income: Double, expense: Double, currencySymbol: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.ArrowDownward,
            label = stringResource(R.string.income),
            amount = CurrencyFormatter.format(income, currencySymbol),
            color = IncomeGreen
        )
        SummaryChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.ArrowUpward,
            label = stringResource(R.string.expense),
            amount = CurrencyFormatter.format(expense, currencySymbol),
            color = ExpenseRed
        )
    }
}

@Composable
private fun SummaryChip(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    amount: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.height(16.dp).width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
