package com.nebulights.coinstacks.Network.exchanges.Gemini

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.exchanges.BaseExchange

/**
* Created by babramovitch on 10/25/2017.
*/

class GeminiRepository(val service: GeminiService) : BaseExchange(), Exchange {
    override fun feedType(): String {
        return CryptoPairs.GEMINI_BTC_USD.exchange
    }

    override fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

        tickers.forEach { ticker ->
            startFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }
    }
}
