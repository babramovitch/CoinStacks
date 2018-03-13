package com.nebulights.coinstacks.Network.exchanges.Bitstamp.model

import com.nebulights.coinstacks.CryptoTypes
import com.nebulights.coinstacks.CurrencyTypes
import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

/**
 * Created by babramovitch on 2018-02-27.
 */
data class Balances(
        val btc_available: String,
        val btc_reserved: String,
        val btc_balance: String,

        val bch_available: String,
        val bch_reserved: String,
        val bch_balance: String,

        val ltc_available: String,
        val ltc_reserved: String,
        val ltc_balance: String,

        val eth_available: String,
        val eth_reserved: String,
        val eth_balance: String,

        val xrp_available: String,
        val xrp_reserved: String,
        val xrp_balance: String,

        val usd_available: String,
        val usd_reserved: String,
        val usd_balance: String,

        val eur_available: String,
        val eur_reserved: String,
        val eur_balance: String

) : NormalizedBalanceData {

    override fun getBalance(currency: String): String = when (currency) {
        CryptoTypes.BTC.name -> btc_balance
        CryptoTypes.BCH.name -> bch_balance
        CryptoTypes.ETH.name -> eth_balance
        CryptoTypes.LTC.name -> ltc_balance
        CryptoTypes.XRP.name -> xrp_balance
        CurrencyTypes.USD.name -> usd_balance
        CurrencyTypes.EUR.name -> eur_balance
        else -> "0"
    }


}