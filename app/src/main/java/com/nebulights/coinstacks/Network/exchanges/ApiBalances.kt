package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes

data class ApiBalances(
        val exchange: String,
        val btcBalance: String,
        val bchBalance: String,
        val ltcBalance: String,
        val ethBalance: String,
        val xrpBalance: String,
        val xmrBalance: String,
        val btgBalance: String,
        val cadBalance: String,
        val usdBalance: String,
        val eurBalance: String,
        val gbpBalance: String) {



    fun getCryptoPairsForNonZeroBalances(cryptoPairMap: MutableMap<CryptoTypes, CryptoPairs>): MutableList<CryptoPairs> {

        val pairs: MutableList<CryptoPairs> = mutableListOf()

        if (btcBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.BTC])
        }

        if (bchBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.BCH])
        }

        if (ltcBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.LTC])
        }

        if (ethBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.ETH])
        }

        if (xrpBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.XRP])
        }

        if (xmrBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.XMR])
        }

        if (btgBalance != "0"){
            addPair(pairs, cryptoPairMap[CryptoTypes.BTG])
        }

        return pairs
    }

    private fun addPair(pairs: MutableList<CryptoPairs>, cryptoPairs: CryptoPairs?) {
        if(cryptoPairs != null) {
            pairs.add(cryptoPairs)
        }
    }


}