package com.nebulights.coinstacks.Network.exchanges.CexIo.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedTickerData


data class CurrentTradingInfo(val timestamp: String,
                              val low: String,
                              val high: String,
                              val last: String,
                              val volume: String,
                              val volume30d: String,
                              val bid: String,
                              val ask: String) : NormalizedTickerData {
    override fun lastPrice(): String = last

    override fun timeStamp(): String = timestamp
}

