package com.nebulights.coinstacks.Network.exchanges.BitFinex.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

data class Balances(
        val type: String,
        val currency: String?,
        val amount: String?,
        val available: String) : NormalizedBalanceData {

    override fun getBalance(currency: String): String {
        return if (this.currency?.toLowerCase() == currency.toLowerCase()) {
            amount ?:"NA"
        }else{
            "NA"
        }
    }
}