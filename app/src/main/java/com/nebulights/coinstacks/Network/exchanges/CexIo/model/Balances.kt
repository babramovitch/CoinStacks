package com.nebulights.coinstacks.Network.exchanges.CexIo.model

import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.CurrencyTypes
import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * Created by babramovitch on 2018-02-25.
 */

data class CexBalances(
        val timestamp: String,
        val username: String,
        val BTC: Balance?,
        val BCH: Balance?,
        val ETH: Balance?,
        val LTC: Balance?,
        val DASH: Balance?,
        val ZEC: Balance?,
        val USD: Balance?,
        val EUR: Balance?,
        val GBP: Balance?,
        val RUB: Balance?,
        val GHS: Balance?
) : NormalizedBalanceData {
    override fun getBalance(currency: String): String = when (currency) {
        CryptoTypes.BTC.name -> balanceString(BTC?.available, BTC?.orders)
        CryptoTypes.BCH.name -> balanceString(BCH?.available, BCH?.orders)
        CryptoTypes.ETH.name -> balanceString(ETH?.available, ETH?.orders)
        CryptoTypes.LTC.name -> balanceString(LTC?.available, LTC?.orders)
    //CryptoTypes.DASH.name ->
    //CryptoTypes.ZEC.name ->
        CurrencyTypes.USD.name -> balanceString(USD?.available, USD?.orders)
        CurrencyTypes.EUR.name -> balanceString(EUR?.available, EUR?.orders)
        CurrencyTypes.GBP.name -> balanceString(GBP?.available, GBP?.orders)
        CurrencyTypes.RUB.name -> balanceString(RUB?.available, RUB?.orders)
    //CurrencyTypes.GHS.name -> usd_balance
        else -> "0"
    }

    private fun balanceString(available: String?, orders: String?): String {
        return if (available != null && orders != null) {
            var amount = BigDecimal(available) + BigDecimal(orders)
            amount = amount.setScale(4, RoundingMode.DOWN)
            amount.toPlainString()
        } else {
            "0"
        }
    }
}

data class Balance(
        val available: String?,
        val orders: String?
)