package com.nebulights.coinstacks.Network.exchanges.Quadriga.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedTickerData

data class CurrentTradingInfo(val timestamp: String,
                              val vwap: String,
                              val last: String,
                              val volume: String,
                              val high: String,
                              val ask: String,
                              val low: String,
                              val bid: String) : NormalizedTickerData {
    override fun lastPrice(): String = last

    override fun timeStamp(): String = timestamp
}

