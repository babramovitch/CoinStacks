package com.nebulights.coinstacks.Network.exchanges.Gemini.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData

/**
 * Created by babramovitch on 2018-02-22.
 */
data class Balances(
        val currency: String,
        val amount: String,
        val available: String,
        val availableForWithdrawal: String
) : NormalizedBalanceData {
    override fun getBchBalance(): String {
        return "hello GEMINI world"
    }
}