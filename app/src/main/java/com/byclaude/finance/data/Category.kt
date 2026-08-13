package com.byclaude.finance.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.byclaude.finance.R

enum class CategoryType { EXPENSE, INCOME }

enum class Category(
    val type: CategoryType,
    val labelRes: Int,
    val icon: ImageVector,
    val color: Color
) {
    FOOD(CategoryType.EXPENSE, R.string.cat_food, Icons.Filled.Restaurant, Color(0xFFEF6C57)),
    TRANSPORT(CategoryType.EXPENSE, R.string.cat_transport, Icons.Filled.DirectionsCar, Color(0xFF4C9AFF)),
    HOUSING(CategoryType.EXPENSE, R.string.cat_housing, Icons.Filled.Home, Color(0xFF9C6ADE)),
    ENTERTAINMENT(CategoryType.EXPENSE, R.string.cat_entertainment, Icons.Filled.LocalMovies, Color(0xFFB983FF)),
    HEALTH(CategoryType.EXPENSE, R.string.cat_health, Icons.Filled.LocalHospital, Color(0xFFFF6E9C)),
    SHOPPING(CategoryType.EXPENSE, R.string.cat_shopping, Icons.Filled.ShoppingBag, Color(0xFFFFA94D)),
    EDUCATION(CategoryType.EXPENSE, R.string.cat_education, Icons.Filled.School, Color(0xFF4DD0E1)),
    SUBSCRIPTIONS(CategoryType.EXPENSE, R.string.cat_subscriptions, Icons.Filled.Subscriptions, Color(0xFF7C83FD)),
    OTHER_EXPENSE(CategoryType.EXPENSE, R.string.cat_other_expense, Icons.Filled.MoreHoriz, Color(0xFF9E9E9E)),

    SALARY(CategoryType.INCOME, R.string.cat_salary, Icons.Filled.Payments, Color(0xFF43A047)),
    GIFT(CategoryType.INCOME, R.string.cat_gift, Icons.Filled.CardGiftcard, Color(0xFFEC407A)),
    INVESTMENTS(CategoryType.INCOME, R.string.cat_investments, Icons.Filled.TrendingUp, Color(0xFF26A69A)),
    OTHER_INCOME(CategoryType.INCOME, R.string.cat_other_income, Icons.Filled.MoreHoriz, Color(0xFF9E9E9E));

    companion object {
        val expenseCategories = entries.filter { it.type == CategoryType.EXPENSE }
        val incomeCategories = entries.filter { it.type == CategoryType.INCOME }
    }
}
