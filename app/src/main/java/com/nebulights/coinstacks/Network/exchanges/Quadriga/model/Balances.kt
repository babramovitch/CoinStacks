package com.nebulights.coinstacks.Network.exchanges.Quadriga.model

import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.CurrencyTypes
import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

/**
 * Created by babramovitch on 2018-02-21.
 */
data class QuadrigaBalances (
        val btc_available: String,
        val btc_reserved: String,
        val btc_balance: String,
        val bch_available: String,
        val bch_reserved: String,
        val bch_balance: String,
        val btg_available: String,
        val btg_reserved: String,
        val btg_balance: String,
        val eth_available: String,
        val eth_reserved: String,
        val eth_balance: String,
        val ltc_available: String,
        val ltc_reserved: String,
        val ltc_balance: String,
        val etc_available: String,
        val etc_reserved: String,
        val etc_balance: String,
        val cad_available: String,
        val cad_reserved: String,
        val cad_balance: String,
        val usd_available: String,
        val usd_reserved: String,
        val usd_balance: String,
        val xau_available: String,
        val xau_reserved: String,
        val xau_balance: String,
        val fee: String,
        val fees: Fees
) : NormalizedBalanceData {

    override fun getBalance(currency: String): String = when(currency) {
        CryptoTypes.BTC.name -> btc_balance
        CryptoTypes.BCH.name -> bch_balance
        CryptoTypes.BTG.name -> btg_balance
        CryptoTypes.ETH.name -> eth_balance
        CryptoTypes.LTC.name -> ltc_balance
        CurrencyTypes.CAD.name -> cad_balance
        CurrencyTypes.USD.name -> usd_balance
        else -> "0"
    }
}

data class Fees(
        val btc_cad: String,
        val btc_usd: String,
        val eth_cad: String,
        val eth_btc: String,
        val ltc_cad: String,
        val ltc_btc: String,
        val bch_cad: String,
        val bch_btc: String,
        val btg_cad: String,
        val btg_btc: String
)