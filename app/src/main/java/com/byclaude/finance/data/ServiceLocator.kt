package com.byclaude.finance.data

import android.content.Context

object ServiceLocator {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val database: AppDatabase by lazy { AppDatabase.getInstance(appContext) }
    val transactionRepository: TransactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
}
