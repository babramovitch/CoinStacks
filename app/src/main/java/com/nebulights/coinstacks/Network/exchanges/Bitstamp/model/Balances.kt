package com.nebulights.coinstacks.Network.exchanges.Bitstamp.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

/**
 * Created by babramovitch on 2018-02-27.
 */
data class Balances (
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
        val fee: String
) : NormalizedBalanceData {

    override fun getBchBalance(): String =
            "Balance:" + bch_balance + " Reserved: " + bch_reserved + " Available: " + bch_available
}