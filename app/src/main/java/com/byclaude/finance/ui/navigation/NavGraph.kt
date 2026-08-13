package com.byclaude.finance.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.byclaude.finance.data.ServiceLocator
import com.byclaude.finance.data.ThemeMode
import com.byclaude.finance.ui.addedit.AddEditScreen
import com.byclaude.finance.ui.home.HomeScreen
import com.byclaude.finance.ui.settings.SettingsScreen
import com.byclaude.finance.ui.stats.StatsScreen
import com.byclaude.finance.ui.theme.MyFinanceTheme
import com.byclaude.finance.ui.transactions.TransactionsScreen

private val topLevelRoutes = setOf(
    Screen.Home.route,
    Screen.Transactions.route,
    Screen.Stats.route,
    Screen.Settings.route
)

@Composable
fun MyFinanceApp() {
    val settingsRepository = ServiceLocator.settingsRepository
    val themeMode by settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

    MyFinanceTheme(themeMode = themeMode) {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val showChrome = currentRoute in topLevelRoutes

            Scaffold(
                bottomBar = {
                    if (showChrome) AppBottomBar(navController = navController, currentRoute = currentRoute)
                },
                floatingActionButton = {
                    if (showChrome) {
                        FloatingActionButton(onClick = {
                            navController.navigate(Screen.AddEdit.createRoute())
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onSeeAll = { navController.navigate(Screen.Transactions.route) },
                            onTransactionClick = { id -> navController.navigate(Screen.AddEdit.createRoute(id)) }
                        )
                    }
                    composable(Screen.Transactions.route) {
                        TransactionsScreen(
                            onTransactionClick = { id -> navController.navigate(Screen.AddEdit.createRoute(id)) }
                        )
                    }
                    composable(Screen.Stats.route) {
                        StatsScreen()
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen()
                    }
                    composable(
                        route = Screen.AddEdit.route,
                        arguments = listOf<NamedNavArgument>(
                            navArgument(ARG_TRANSACTION_ID) {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { entry ->
                        val id = entry.arguments?.getLong(ARG_TRANSACTION_ID) ?: -1L
                        AddEditScreen(
                            transactionId = if (id == -1L) null else id,
                            onDone = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) }
            )
        }
    }
}
