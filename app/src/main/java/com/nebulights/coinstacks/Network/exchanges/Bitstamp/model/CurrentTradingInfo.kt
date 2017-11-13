package com.nebulights.coinstacks.Network.exchanges.Bitstamp.model

import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.NormalizedTickerData

data class CurrentTradingInfo(val last: String,
                              val high: String,
                              val low: String,
                              val vwap: String,
                              val volume: String,
                              val bid: String,
                              val ask: String,
                              val timestamp: String,
                              val open: String) : NormalizedTickerData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return timestamp
    }
}