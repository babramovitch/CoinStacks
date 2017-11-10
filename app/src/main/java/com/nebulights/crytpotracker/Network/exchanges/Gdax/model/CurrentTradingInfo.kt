package com.nebulights.crytpotracker.Network.exchanges.Gdax.model

import com.nebulights.crytpotracker.Network.Bitfinex.model.normalizedData


data class CurrentTradingInfo(val trade_id: String,
                              val price: String,
                              val size: String,
                              val bid: String,
                              val ask: String,
                              val volume: String,
                              val time: String) : normalizedData {
    override fun lastPrice(): String {
        return price
    }

    override fun timeStamp(): String {
        return time
    }
}