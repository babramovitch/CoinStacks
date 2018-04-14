package com.nebulights.coinstacks.Network.exchanges.BitFinex

import android.util.Log
import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.*
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitFinexRepository(val service: BitFinexService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = false
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.BITFINEX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearTickerDisposables()

        launch(CommonPool) {
            tickers.forEach { ticker ->
                startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                        ticker, presenterCallback, exchangeNetworkDataUpdate)

                if (tickers.size > Constants.rateLimitSizeThreshold) {
                    delay(Constants.tickerDelayInMillis)
                }
            }
        }
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
