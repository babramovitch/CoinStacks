package com.nebulights.coinstacks.Network.exchanges.Gemini.model

/**
 * Created by babramovitch on 2018-02-22.
 */
data class BalanceRequest(
        val request: String,
        val nonce: Long
)