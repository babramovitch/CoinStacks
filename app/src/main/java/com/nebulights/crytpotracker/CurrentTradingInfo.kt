package com.nebulights.crytpotracker

class CurrentTradingInfo {
    var timestamp: String? = null
    var vwap: String? = null
    var last: String? = null
    var volume: String? = null
    var high: String? = null
    var ask: String? = null
    var low: String? = null
    var bid: String? = null

    override fun toString(): String {
        return "ClassPojo [timestamp = $timestamp, vwap = $vwap, last = $last, volume = $volume, high = $high, ask = $ask, low = $low, bid = $bid]"
    }
}