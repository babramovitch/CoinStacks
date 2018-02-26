package com.nebulights.coinstacks.Network.exchanges.CexIo.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData


/**
 * Created by babramovitch on 2018-02-25.
 */

data class CexBalances(
        val timestamp: String,
        val username: String,
        val BTC: Balance,
        val BCH: Balance,
        val ETH: Balance,
        val LTC: Balance,
        val DASH: Balance,
        val ZEC: Balance,
        val USD: Balance,
        val EUR: Balance,
        val GBP: Balance,
        val RUB: Balance,
        val GHS: Balance
) : NormalizedBalanceData {
    override fun getBchBalance(): String = BCH.available + " " + BCH.orders
}

data class Balance(
        val available: String,
        val orders: String
)