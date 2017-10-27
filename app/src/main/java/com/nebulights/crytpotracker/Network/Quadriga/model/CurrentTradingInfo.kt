package com.nebulights.crytpotracker.Network.Quadriga.model

class CurrentTradingInfo {
    var ticker: String? = null
    var timestamp: String? = null
    var vwap: String? = null
    var last: String? = null
    var volume: String? = null
    var high: String? = null
    var ask: String? = null
    var low: String? = null
    var bid: String? = null

    override fun toString(): String {
        return "ClassPojo [ticker = $ticker timestamp = $timestamp, vwap = $vwap, last = $last, volume = $volume, high = $high, ask = $ask, low = $low, bid = $bid]"
    }
}