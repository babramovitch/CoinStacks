package com.nebulights.coinstacks.Network.BlockExplorers.RippleExplorer.Model

import com.nebulights.coinstacks.Network.BlockExplorers.NormalizedEthereumBalanceData
import java.math.BigDecimal

/**
 * Created by babramovitch on 4/13/2018.
 *
 */

data class AddressBalance(
        val result: String,
        val ledger_index: String,
        val limit: Int,
        val balances: List<Balance>
) : NormalizedEthereumBalanceData {
    override fun getAddressBalance(): BigDecimal {
        return BigDecimal(balances[0].value)
    }
}

data class Balance(
        val currency: String,
        val value: String
)