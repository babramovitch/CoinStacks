package com.nebulights.coinstacks.Network.exchanges.Gemini.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedTickerData

data class CurrentTradingInfo(val ask: String,
                              val bid: String,
                              val last: String?,
                              val volume: Volume) : NormalizedTickerData {

    override fun lastPrice(): String = last ?: "0"

    override fun timeStamp(): String = volume.timestamp
}

data class Volume(val BTC: String, val USD: String, val timestamp: String)
