package com.nebulights.coinstacks.Network

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.TradingInfo
import io.reactivex.Observable

/**
* Created by babramovitch on 11/9/2017.
*/

interface NetworkDataUpdate {
    fun updateData(ticker: CryptoPairs, data: TradingInfo)
}

interface NetworkCompletionCallback {
    fun updateUi(ticker: CryptoPairs)
}

interface Exchange {
    fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
    fun stopFeed()
    fun feedType(): String
}

object Exchanges : NetworkDataUpdate {

    private var tickerData: MutableMap<CryptoPairs, TradingInfo> = mutableMapOf()
    private lateinit var repositories: List<Exchange>

    fun loadRepositories(exchangeProvider: ExchangeProvider) {
        repositories = exchangeProvider.getAllRepositories()
    }

    fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback) {
        repositories.forEach { repository ->

            val filteredTickers = getTickers(tickers, repository.feedType())

            if (filteredTickers.isNotEmpty()) {
                repository.startFeed(filteredTickers, presenterCallback, this)
            }
        }
    }

    private fun getTickers(tickers: List<CryptoPairs>, exchange: String): List<CryptoPairs> {
        return tickers.filter { ticker ->
            ticker.exchange == exchange
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

    fun clearData() {
        tickerData.clear()
    }

    override fun updateData(ticker: CryptoPairs, data: TradingInfo) {
        tickerData.put(ticker, data)
    }
}
