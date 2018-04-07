package com.nebulights.coinstacks.Network.exchanges.BitFinex

import com.nebulights.coinstacks.Network.*
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication

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

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate) {

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
