package com.nebulights.coinstacks.Network.exchanges.CexIo.model

import com.nebulights.coinstacks.CryptoTypes
import com.nebulights.coinstacks.CurrencyTypes
import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData
import java.math.BigDecimal


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
    override fun getBalance(currency: String): String = when(currency) {
        CryptoTypes.BTC.name -> (BigDecimal(BTC.available) + BigDecimal(BTC.orders)).toString()
        CryptoTypes.BCH.name -> (BigDecimal(BCH.available) + BigDecimal(BCH.orders)).toString()
        CryptoTypes.ETH.name -> (BigDecimal(ETH.available) + BigDecimal(ETH.orders)).toString()
        CryptoTypes.LTC.name -> (BigDecimal(LTC.available) + BigDecimal(LTC.orders)).toString()
        //CryptoTypes.DASH.name ->
        //CryptoTypes.ZEC.name ->
        CurrencyTypes.USD.name -> (BigDecimal(USD.available) + BigDecimal(USD.orders)).toString()
        CurrencyTypes.EUR.name -> (BigDecimal(EUR.available) + BigDecimal(EUR.orders)).toString()
        CurrencyTypes.GBP.name ->  (BigDecimal(GBP.available) + BigDecimal(GBP.orders)).toString()
        CurrencyTypes.RUB.name ->  (BigDecimal(RUB.available) + BigDecimal(RUB.orders)).toString()
        //CurrencyTypes.GHS.name -> usd_balance
        else -> "0"
    }
}

data class Balance(
        val available: String,
        val orders: String
)