package com.nebulights.coinstacks.Network.exchanges.BitFinex

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitFinexRepository(val service: BitFinexService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = false
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.BITFINEX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearTickerDisposables()

        tickers.forEach { ticker ->
            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
