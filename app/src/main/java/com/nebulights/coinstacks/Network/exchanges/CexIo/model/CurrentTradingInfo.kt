package com.nebulights.coinstacks.Network.exchanges.CexIo.model

import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.NormalizedTickerData

data class CurrentTradingInfo(val timestamp: String,
                              val low: String,
                              val high: String,
                              val last: String,
                              val volume: String,
                              val volume30d: String,
                              val bid: String,
                              val ask: String) : NormalizedTickerData {
    override fun lastPrice(): String {
        return last
    }

    override fun timeStamp(): String {
        return timestamp
    }
}

