package com.nebulights.coinstacks.Network

import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.userTicker

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

interface ApiKeyValidationCallback {
    fun validationSuccess()
    fun validationError(errorBody: String?)
}

interface Exchange {
    fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
    fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
    fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate)
    fun stopFeed()
    fun feedType(): String
    fun userNameRequired(): Boolean
    fun passwordRequired(): Boolean
}

object Exchanges : NetworkDataUpdate {

    private var allTickers = enumValues<CryptoPairs>().map { it }
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

    fun validateExchange(authentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback) {
        repositories.forEach { repository ->
            if (repository.feedType() == authentication.exchange) {
                repository.validateApiKeys(authentication, presenterCallback, this)
            }
        }
    }

    private fun getTickers(tickers: List<CryptoPairs>, exchange: String): List<CryptoPairs> =
            tickers.filter { ticker ->
                ticker.exchange == exchange
            }

    fun getTickersForExchange(exchange: String): List<String> =
            allTickers.filter { ticker ->
                ticker.exchange.toLowerCase() == exchange.toLowerCase()
            }.map { ticker -> ticker.userTicker() }


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

    fun getData(): MutableMap<CryptoPairs, TradingInfo> = tickerData

    fun getApiData(): MutableMap<String, ApiBalances> = apiData

    fun clearData() {
        tickerData.clear()
    }

    fun userNameRequiredForAuthentication(exchange: String): Boolean =
            repositories.first { details ->
                details.feedType() == exchange
            }.userNameRequired()

    fun passwordRequiredForAuthentication(exchange: String): Boolean =
            repositories.first { details ->
                details.feedType() == exchange
            }.passwordRequired()

    override fun updateData(ticker: CryptoPairs, data: TradingInfo) {
        tickerData[ticker] = data
    }

    override fun updateApiData(exchange: String, data: ApiBalances) {
        apiData[exchange] = data
    }
}
