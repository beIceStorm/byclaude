package com.byclaude.finance.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byclaude.finance.R
import com.byclaude.finance.data.ServiceLocator
import com.byclaude.finance.data.Transaction
import com.byclaude.finance.ui.SimpleViewModelFactory
import com.byclaude.finance.ui.components.EmptyState
import com.byclaude.finance.ui.components.TransactionRow
import com.byclaude.finance.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onTransactionClick: (Long) -> Unit,
    viewModel: TransactionsViewModel = viewModel(
        factory = SimpleViewModelFactory {
            TransactionsViewModel(ServiceLocator.transactionRepository, ServiceLocator.settingsRepository)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isLoading && uiState.groupedTransactions.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.empty_transactions_title),
            subtitle = stringResource(R.string.empty_transactions_subtitle)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        uiState.groupedTransactions.forEach { (date, transactions) ->
            item(key = "header_${date}") {
                Text(
                    text = DateUtils.formatDayHeader(date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(transactions, key = { it.id }) { transaction ->
                SwipeToDeleteRow(
                    transaction = transaction,
                    currencySymbol = uiState.currencySymbol,
                    onClick = { onTransactionClick(transaction.id) },
                    onDelete = { viewModel.delete(transaction) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteRow(
    transaction: Transaction,
    currencySymbol: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            TransactionRow(
                transaction = transaction,
                currencySymbol = currencySymbol,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
