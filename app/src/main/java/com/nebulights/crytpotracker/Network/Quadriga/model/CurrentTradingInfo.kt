package com.nebulights.crytpotracker.Network.Quadriga.model

data class CurrentTradingInfo(val timestamp: String,
                              val vwap: String,
                              val last: String,
                              val volume: String,
                              val high: String,
                              val ask: String,
                              val low: String,
                              val bid: String
)