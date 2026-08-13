package com.byclaude.finance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.byclaude.finance.R

const val ARG_TRANSACTION_ID = "transactionId"

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Transactions : Screen("transactions")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
    data object AddEdit : Screen("add_edit?$ARG_TRANSACTION_ID={$ARG_TRANSACTION_ID}") {
        fun createRoute(transactionId: Long? = null) =
            if (transactionId == null) "add_edit" else "add_edit?$ARG_TRANSACTION_ID=$transactionId"
    }
}

data class BottomNavItem(val screen: Screen, val labelRes: Int, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_home, Icons.Filled.Home),
    BottomNavItem(Screen.Transactions, R.string.nav_transactions, Icons.Filled.List),
    BottomNavItem(Screen.Stats, R.string.nav_stats, Icons.Filled.PieChart),
    BottomNavItem(Screen.Settings, R.string.nav_settings, Icons.Filled.Settings)
)
