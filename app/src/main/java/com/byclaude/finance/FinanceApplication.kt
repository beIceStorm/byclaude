package com.byclaude.finance

import android.app.Application
import com.byclaude.finance.data.ServiceLocator

class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
