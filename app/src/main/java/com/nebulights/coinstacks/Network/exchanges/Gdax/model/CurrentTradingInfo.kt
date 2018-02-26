package com.nebulights.coinstacks.Network.exchanges.Gdax.model

import com.nebulights.coinstacks.Network.exchanges.NormalizedTickerData

data class CurrentTradingInfo(val trade_id: String,
                              val price: String,
                              val size: String,
                              val bid: String,
                              val ask: String,
                              val volume: String,
                              val time: String) : NormalizedTickerData {

    override fun lastPrice(): String = price

    override fun timeStamp(): String = time
}