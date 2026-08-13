package com.byclaude.finance.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byclaude.finance.R
import com.byclaude.finance.data.CURRENCY_OPTIONS
import com.byclaude.finance.data.ServiceLocator
import com.byclaude.finance.data.ThemeMode
import com.byclaude.finance.ui.SimpleViewModelFactory
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SimpleViewModelFactory {
            SettingsViewModel(ServiceLocator.settingsRepository, ServiceLocator.transactionRepository)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        SectionLabel(stringResource(R.string.settings_currency))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(CURRENCY_OPTIONS, key = { it.code }) { option ->
                FilterChip(
                    selected = uiState.currencySymbol == option.symbol,
                    onClick = { viewModel.setCurrency(option.symbol) },
                    label = { Text("${option.symbol} ${option.code}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel(stringResource(R.string.settings_theme))
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val options = listOf(
                Triple(ThemeMode.SYSTEM, R.string.theme_system, 0),
                Triple(ThemeMode.LIGHT, R.string.theme_light, 1),
                Triple(ThemeMode.DARK, R.string.theme_dark, 2)
            )
            options.forEach { (mode, labelRes, index) ->
                SegmentedButton(
                    selected = uiState.themeMode == mode,
                    onClick = { viewModel.setThemeMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                ) { Text(stringResource(labelRes)) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_clear_data)) },
            supportingContent = { Text(stringResource(R.string.settings_clear_data_desc)) },
            leadingContent = {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier.clickable { showClearConfirm = true }
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(stringResource(R.string.settings_about), style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.settings_about_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.clear_data_confirm_title)) },
            text = { Text(stringResource(R.string.clear_data_confirm_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    viewModel.clearAllData()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
