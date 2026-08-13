package com.byclaude.finance.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class CurrencyOption(val code: String, val symbol: String)

val CURRENCY_OPTIONS = listOf(
    CurrencyOption("RUB", "₽"),
    CurrencyOption("USD", "$"),
    CurrencyOption("EUR", "€"),
    CurrencyOption("KZT", "₸"),
    CurrencyOption("UAH", "₴"),
    CurrencyOption("BYN", "Br"),
    CurrencyOption("GBP", "£")
)

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val CURRENCY = stringPreferencesKey("currency_symbol")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val currencySymbol: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.CURRENCY] ?: CURRENCY_OPTIONS.first().symbol
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
    }

    suspend fun setCurrency(symbol: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = symbol }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }
}
