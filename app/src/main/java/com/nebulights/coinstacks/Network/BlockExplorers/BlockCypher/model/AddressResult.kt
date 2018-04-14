package com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher.model

import com.nebulights.coinstacks.Network.BlockExplorers.NormalizedEthereumBalanceData
import java.math.BigDecimal

/**
 * Created by babramovitch on 4/13/2018.
 *
 */

data class AddressResult(
        val address: String,
        val total_received: String,
        val total_sent: String,
        val balance: String,
        val unconfirmed_balance: String,
        val final_balance: String,
        val n_tx: String,
        val unconfirmed_n_tx: String,
        val final_n_tx: String,
        val nonce: String,
        val pool_nonce: String
) : NormalizedEthereumBalanceData {
    override fun getAddressBalance(): BigDecimal {
        return (BigDecimal(balance).setScale(22) / BigDecimal("10000000000000000000000").setScale(22)).stripTrailingZeros()
    }
}