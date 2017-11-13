package com.nebulights.coinstacks.Network.exchanges.Quadriga.model

import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.NormalizedTickerData

data class CurrentTradingInfo(val timestamp: String,
                              val vwap: String,
                              val last: String,
                              val volume: String,
                              val high: String,
                              val ask: String,
                              val low: String,
                              val bid: String) : NormalizedTickerData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return timestamp
    }
}

