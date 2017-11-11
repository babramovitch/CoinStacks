package com.nebulights.crytpotracker.Network.exchanges.Bitstamp.model

import com.nebulights.crytpotracker.Network.exchanges.BitFinex.model.normalizedData

data class CurrentTradingInfo(val last: String,
                              val high: String,
                              val low: String,
                              val vwap: String,
                              val volume: String,
                              val bid: String,
                              val ask: String,
                              val timestamp: String,
                              val open: String) : normalizedData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return timestamp
    }
}