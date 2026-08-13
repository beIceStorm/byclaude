package com.byclaude.finance.util

import kotlin.math.abs
import kotlin.math.round

object CurrencyFormatter {

    fun format(amount: Double, symbol: String, showPlusSign: Boolean = false): String {
        val rounded = round(amount * 100) / 100.0
        val isNegative = rounded < 0
        val absValue = abs(rounded)
        val wholePart = absValue.toLong()
        val fraction = Math.round((absValue - wholePart) * 100).toInt()

        val groupedWhole = groupThousands(wholePart)
        val amountText = if (fraction == 0) groupedWhole else "$groupedWhole,${fraction.toString().padStart(2, '0')}"

        val sign = when {
            isNegative -> "-"
            showPlusSign -> "+"
            else -> ""
        }
        return "$sign$amountText $symbol"
    }

    private fun groupThousands(value: Long): String {
        val digits = value.toString()
        val builder = StringBuilder()
        for ((index, char) in digits.reversed().withIndex()) {
            if (index != 0 && index % 3 == 0) builder.append(' ')
            builder.append(char)
        }
        return builder.reverse().toString()
    }
}
