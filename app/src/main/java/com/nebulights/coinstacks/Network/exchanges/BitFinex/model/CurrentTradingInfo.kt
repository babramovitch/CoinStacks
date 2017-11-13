package com.nebulights.coinstacks.Network.exchanges.BitFinex.model

interface NormalizedTickerData {
    fun lastPrice(): String
    fun timeStamp(): String
}

data class CurrentTradingInfo(val mid: String,
                              val bid: String,
                              val ask: String,
                              val last_price: String,
                              val low: String,
                              val high: String,
                              val volume: String,
                              val timestamp: String) : NormalizedTickerData {
    override fun lastPrice(): String {
        return last_price
    }

    override fun timeStamp(): String {
        return timestamp
    }
}