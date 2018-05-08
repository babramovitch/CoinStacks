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
    override val userNameText = ""

    override fun feedType(): String = ExchangeProvider.BITFINEX_NAME

    override fun userNameText(): String {
        return userNameText
    }

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearTickerDisposables()
        addToPriceFeed(tickers,presenterCallback,exchangeNetworkDataUpdate)
    }

    override fun addToPriceFeed(
        tickers: List<CryptoPairs>,
        presenterCallback: NetworkCompletionCallback,
        exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate
    ) {
        var delay = 0L

        launch {
            tickers.forEach { ticker ->
                startPriceFeed(service.getCurrentTradingInfo(ticker.ticker), delay,
                    ticker, presenterCallback, exchangeNetworkDataUpdate)

                if (totalDisposables() > Constants.rateLimitSizeThreshold) {
                    delay += 5000
                }

                delay( 500)
            }
        }
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
