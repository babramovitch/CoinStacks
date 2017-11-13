package com.nebulights.coinstacks.Network.exchanges.Gemini.model

import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.NormalizedTickerData

data class CurrentTradingInfo(val ask: String,
                              val bid: String,
                              val last: String,
                              val volume: Volume) : NormalizedTickerData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return volume.timestamp
    }
}

data class Volume(val BTC: String, val USD: String, val timestamp: String )
