package com.nebulights.coinstacks.Network.exchanges.Gdax.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

/**
 * Created by babramovitch on 2018-02-22.
 */

data class Balances(
        val id: String,
        val currency: String?,
        val balance: String?,
        val available: String,
        val hold: String,
        val profile_id: String
) : NormalizedBalanceData {

    override fun getBalance(currency: String): String {
        return if (this.currency?.toLowerCase() == currency.toLowerCase()) {
            balance ?: "NA"
        }else{
            "NA"
        }
    }
}
