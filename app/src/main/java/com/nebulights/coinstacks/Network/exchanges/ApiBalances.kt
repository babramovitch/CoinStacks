package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes
import java.math.BigDecimal

data class ApiBalances(
        val exchange: String,
        val btcBalance: BigDecimal,
        val bchBalance: BigDecimal,
        val ltcBalance: BigDecimal,
        val ethBalance: BigDecimal,
        val xrpBalance: BigDecimal,
        val xmrBalance: BigDecimal,
        val btgBalance: BigDecimal,
        val cadBalance: BigDecimal,
        val usdBalance: BigDecimal,
        val eurBalance: BigDecimal,
        val gbpBalance: BigDecimal) {


    companion object {
        fun create(exchange: String, normalizedBalanceData: NormalizedBalanceData): ApiBalances {
            return ApiBalances(exchange,
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"))
        }

        fun create(exchange: String, normalizedBalanceData: Array<NormalizedBalanceData>): ApiBalances {
            return ApiBalances(exchange,
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"), BigDecimal("1"),
                    BigDecimal("1"), BigDecimal("1"))
        }
    }

    fun getCryptoPairsForNonZeroBalances(cryptoPairMap: MutableMap<CryptoTypes, CryptoPairs>): MutableList<CryptoPairs> {

        val pairs: MutableList<CryptoPairs> = mutableListOf()

        if (btcBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BTC])
        }

        if (bchBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BCH])
        }

        if (ltcBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.LTC])
        }

        if (ethBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.ETH])
        }

        if (xrpBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.XRP])
        }

        if (xmrBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.XMR])
        }

        if (btgBalance != BigDecimal.ZERO) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BTG])
        }

        return pairs
    }

    private fun addPair(pairs: MutableList<CryptoPairs>, cryptoPairs: CryptoPairs?) {
        if (cryptoPairs != null) {
            pairs.add(cryptoPairs)
        }
    }
}