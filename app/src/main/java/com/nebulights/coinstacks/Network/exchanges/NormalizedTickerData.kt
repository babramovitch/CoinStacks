package com.nebulights.coinstacks.Network.exchanges

/**
 * Created by babramovitch on 2018-02-25.
 */
interface NormalizedTickerData {
    fun lastPrice(): String
    fun timeStamp(): String
}