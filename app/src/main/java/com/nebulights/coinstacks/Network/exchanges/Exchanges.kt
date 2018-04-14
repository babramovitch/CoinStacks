package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressBalance
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.userTicker

/**
 * Created by babramovitch on 11/9/2017.
 */

interface ExchangeNetworkDataUpdate {
    fun updateData(ticker: CryptoPairs, data: TradingInfo)
    fun updateApiData(exchange: String, data: ApiBalances)
}

interface NetworkCompletionCallback {
    fun updateUi(ticker: CryptoPairs)
    fun updateUi(apiBalances: ApiBalances)
    fun updateUi(watchAddress: WatchAddressBalance)
}

interface Exchange {
    fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate)
    fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate)
    fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate)
    fun stopFeed()
    fun feedType(): String
    fun userNameRequired(): Boolean
    fun passwordRequired(): Boolean
}

object Exchanges : ExchangeNetworkDataUpdate {

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

    fun validateExchange(authentication: BasicAuthentication, presenterCallback: ValidationCallback) {
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
