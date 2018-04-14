package com.nebulights.coinstacks.Network.BlockExplorers

import java.math.BigDecimal

/**
 * Created by babramovitch on 2018-02-25.
 */
interface NormalizedEthereumBalanceData {
    fun getAddressBalance(): BigDecimal
}