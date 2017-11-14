package com.nebulights.coinstacks.Network.exchanges.BitFinex

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.BaseExchange

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitFinexRepository(val service: BitFinexService) : BaseExchange(), Exchange {

    override fun feedType(): String {
        return ExchangeProvider.BITFINEX_NAME
    }

    override fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

        tickers.forEach { ticker ->
            startFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }
    }
}
