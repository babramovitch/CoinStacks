package com.nebulights.coinstacks.Network.exchanges.Bitstamp.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedTickerData

data class CurrentTradingInfo(val last: String?,
                              val high: String,
                              val low: String,
                              val vwap: String,
                              val volume: String,
                              val bid: String,
                              val ask: String,
                              val timestamp: String,
                              val open: String) : NormalizedTickerData {
    override fun lastPrice(): String = last ?: "0"

    override fun timeStamp(): String = timestamp
}