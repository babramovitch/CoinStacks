package com.nebulights.crytpotracker.Network.exchanges.Gemini.model

import com.nebulights.crytpotracker.Network.Bitfinex.model.normalizedData

data class CurrentTradingInfo(val ask: String,
                              val bid: String,
                              val last: String,
                              val volume: Volume) : normalizedData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return volume.timestamp
    }
}

data class Volume(val BTC: String, val USD: String, val timestamp: String )
