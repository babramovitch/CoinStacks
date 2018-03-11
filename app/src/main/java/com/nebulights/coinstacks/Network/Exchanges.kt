package com.nebulights.coinstacks.Network

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.TradingInfo

/**
 * Created by babramovitch on 11/9/2017.
 */

interface NetworkDataUpdate {
    fun updateData(ticker: CryptoPairs, data: TradingInfo)
    fun updateApiData(exchange: String, data: ApiBalances)
}

interface NetworkCompletionCallback {
    fun updateUi(ticker: CryptoPairs)
    fun updateUi(apiBalances: ApiBalances)
}

interface Exchange {
    fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
    fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
    fun stopFeed()
    fun feedType(): String
    fun userNameRequired(): Boolean
    fun passwordRequired(): Boolean

}

object Exchanges : NetworkDataUpdate {

    private var tickerData: MutableMap<CryptoPairs, TradingInfo> = mutableMapOf()
    private var apiData: MutableMap<String, ApiBalances> = mutableMapOf()
    private lateinit var repositories: List<Exchange>

    fun loadRepositories(exchangeProvider: ExchangeProvider) {
        repositories = exchangeProvider.getAllRepositories()
    }

    fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback) {
        repositories.forEach { repository ->

            val filteredTickers = getTickers(tickers, repository.feedType())

            if (filteredTickers.isNotEmpty()) {
                repository.startPriceFeed(filteredTickers, presenterCallback, this)
            }
        }
    }

    fun startBalanceFeed(authenticationDetails: List<BasicAuthentication>, presenterCallback: NetworkCompletionCallback) {
        repositories.forEach { repository ->

            val filteredAuthentication = getAuthenticationDetails(authenticationDetails, repository.feedType())

            if (filteredAuthentication.isNotEmpty()) {
                repository.startAccountFeed(filteredAuthentication[0], presenterCallback, this)
            }
        }
    }

    private fun getTickers(tickers: List<CryptoPairs>, exchange: String): List<CryptoPairs> {
        return tickers.filter { ticker ->
            ticker.exchange == exchange
        }
    }

    private fun getAuthenticationDetails(authenticationDetails: List<BasicAuthentication>, exchange: String): List<BasicAuthentication> {
        return authenticationDetails.filter { details ->
            details.exchange == exchange
        }
    }

    fun stopFeed() {
        repositories.forEach { repository ->
            repository.stopFeed()
        }
    }

    fun getData(): MutableMap<CryptoPairs, TradingInfo> {
        return tickerData
    }

    fun getApiData(): MutableMap<String, ApiBalances> {
        return apiData
    }

    fun clearData() {
        tickerData.clear()
    }

    fun userNameRequiredForAuthentication(exchange: String): Boolean {
        val exchanges = repositories.filter { details ->
            details.feedType() == exchange
        }

        return exchanges[0].userNameRequired()
    }

    fun passwordRequiredForAuthentication(exchange: String): Boolean {
        val exchanges = repositories.filter { details ->
            details.feedType() == exchange
        }

        return exchanges[0].passwordRequired()
    }

    override fun updateData(ticker: CryptoPairs, data: TradingInfo) {
        tickerData.put(ticker, data)
    }

    override fun updateApiData(exchange: String, data: ApiBalances) {
        apiData.put(exchange, data)
    }

    fun getTickersForExchange(exchange: String) {

    }
}
