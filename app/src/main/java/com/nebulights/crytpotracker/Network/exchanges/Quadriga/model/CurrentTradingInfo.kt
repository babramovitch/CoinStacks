package com.nebulights.crytpotracker.Network.exchanges.Quadriga.model

import com.nebulights.crytpotracker.Network.Bitfinex.model.normalizedData

data class CurrentTradingInfo(val timestamp: String,
                              val vwap: String,
                              val last: String,
                              val volume: String,
                              val high: String,
                              val ask: String,
                              val low: String,
                              val bid: String) : normalizedData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return timestamp
    }
}

