package com.nebulights.coinstacks.Network.exchanges.Models

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.CurrencyTypes
import com.nebulights.coinstacks.Network.exchanges.NormalizedBalanceData
import java.math.BigDecimal

data class ApiBalances(
        val exchange: String,
        val displayBalancesAs: MutableMap<CryptoTypes, CryptoPairs>,
        val btcBalance: BigDecimal = BigDecimal.ZERO,
        val bchBalance: BigDecimal = BigDecimal.ZERO,
        val ltcBalance: BigDecimal = BigDecimal.ZERO,
        val ethBalance: BigDecimal = BigDecimal.ZERO,
        val xrpBalance: BigDecimal = BigDecimal.ZERO,
        val xmrBalance: BigDecimal = BigDecimal.ZERO,
        val btgBalance: BigDecimal = BigDecimal.ZERO,
        val cadBalance: BigDecimal = BigDecimal.ZERO,
        val usdBalance: BigDecimal = BigDecimal.ZERO,
        val eurBalance: BigDecimal = BigDecimal.ZERO,
        val gbpBalance: BigDecimal = BigDecimal.ZERO,
        val rubBalance: BigDecimal = BigDecimal.ZERO) {

    companion object {
        fun create(exchange: String, displayBalances: MutableMap<CryptoTypes, CryptoPairs>, data: NormalizedBalanceData): ApiBalances {

            return ApiBalances(exchange,
                    displayBalances,
                    BigDecimal(data.getBalance(CryptoTypes.BTC.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.BCH.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.LTC.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.ETH.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.XRP.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.XMR.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CryptoTypes.BTG.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CurrencyTypes.USD.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CurrencyTypes.EUR.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CurrencyTypes.GBP.name)).stripTrailingZeros(),
                    BigDecimal(data.getBalance(CurrencyTypes.RUB.name)).stripTrailingZeros())

        }

        fun create(exchange: String, displayBalances: MutableMap<CryptoTypes, CryptoPairs>, data: Array<NormalizedBalanceData>): ApiBalances =
                ApiBalances(exchange,
                        displayBalances,
                        findBalance(CryptoTypes.BTC.name, data),
                        findBalance(CryptoTypes.BCH.name, data),
                        findBalance(CryptoTypes.LTC.name, data),
                        findBalance(CryptoTypes.ETH.name, data),
                        findBalance(CryptoTypes.XRP.name, data),
                        findBalance(CryptoTypes.XMR.name, data),
                        findBalance(CryptoTypes.BTG.name, data),
                        findBalance(CurrencyTypes.CAD.name, data),
                        findBalance(CurrencyTypes.USD.name, data),
                        findBalance(CurrencyTypes.EUR.name, data),
                        findBalance(CurrencyTypes.GBP.name, data),
                        findBalance(CurrencyTypes.RUB.name, data)
                )

        private fun findBalance(currency: String, data: Array<NormalizedBalanceData>): BigDecimal {
            data.forEach {
                val balance = it.getBalance(currency)
                if (balance != "NA") {
                    return BigDecimal(balance)
                }
            }
            return BigDecimal.ZERO
        }
    }

    fun getCryptoPairsForNonZeroBalances(cryptoPairMap: MutableMap<CryptoTypes, CryptoPairs>): MutableList<CryptoPairs> {

        val pairs: MutableList<CryptoPairs> = mutableListOf()

        val zero = BigDecimal("0.0").setScale(8)

        if (btcBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BTC])
        }

        if (bchBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BCH])
        }

        if (ltcBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.LTC])
        }

        if (ethBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.ETH])
        }

        if (xrpBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.XRP])
        }

        if (xmrBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.XMR])
        }

        if (btgBalance.setScale(8) != zero) {
            addPair(pairs, cryptoPairMap[CryptoTypes.BTG])
        }

        return pairs
    }

     fun getNonZeroFiatBalances() : ArrayList<CurrencyTypes> {
        val nonZeroFiatBalances: ArrayList<CurrencyTypes> = ArrayList()

        CurrencyTypes.values().forEach {
            if(getBalance(it.name) > BigDecimal.ZERO) {
                nonZeroFiatBalances.add(it)
            }

        }
        return nonZeroFiatBalances
    }

    private fun addPair(pairs: MutableList<CryptoPairs>, cryptoPairs: CryptoPairs?) {
        if (cryptoPairs != null) {
            pairs.add(cryptoPairs)
        }
    }

    fun getBalance(currency: String): BigDecimal = when (currency) {
        CryptoTypes.BTC.name -> btcBalance
        CryptoTypes.BCH.name -> bchBalance
        CryptoTypes.LTC.name -> ltcBalance
        CryptoTypes.ETH.name -> ethBalance
        CryptoTypes.BTG.name -> btgBalance
        CryptoTypes.XMR.name -> xmrBalance
        CryptoTypes.XRP.name -> xrpBalance
        CurrencyTypes.CAD.name -> cadBalance
        CurrencyTypes.USD.name -> usdBalance
        CurrencyTypes.EUR.name -> eurBalance
        CurrencyTypes.GBP.name -> gbpBalance
        CurrencyTypes.RUB.name -> rubBalance
        else -> BigDecimal.ZERO
    }
}