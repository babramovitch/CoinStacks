package com.nebulights.coinstacks.Network.BlockExplorers

import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs

/**
 * Created by babramovitch on 11/9/2017.
 */

interface Explorer {
    fun stopFeed()
    fun startAddressFeed(address: ArrayList<String>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate)
}

object Explorers : NetworkDataUpdate {


    // private var allTickers = enumValues<CryptoPairs>().map { it }
    // private var tickerData: MutableMap<CryptoPairs, TradingInfo> = mutableMapOf()
    // private var apiData: MutableMap<String, ApiBalances> = mutableMapOf()
    private lateinit var repositories: List<Explorer>


    fun loadRepositories(explorerProvider: ExplorerProvider) {
        repositories = explorerProvider.getAllRepositories()
    }

    fun startFeed(address: ArrayList<String>, presenterCallback: NetworkCompletionCallback) {
        repositories.forEach { repository ->
            //TODO ensure this is the correct repository to query, will need additional inputs in the startFeed to do this
            repository.startAddressFeed(address, presenterCallback, this)
        }
    }


    fun stopFeed() {
        repositories.forEach { repository ->
            repository.stopFeed()
        }
    }

    override fun updateData(ticker: CryptoPairs, data: TradingInfo) {
    }

    override fun updateApiData(exchange: String, data: ApiBalances) {
    }

    // fun getData(): MutableMap<CryptoPairs, TradingInfo> = tickerData

    // fun getApiData(): MutableMap<String, ApiBalances> = apiData

//    fun clearData() {
//        tickerData.clear()
//    }

//    fun userNameRequiredForAuthentication(exchange: String): Boolean =
//            repositories.first { details ->
//                details.feedType() == exchange
//            }.userNameRequired()
//
//    fun passwordRequiredForAuthentication(exchange: String): Boolean =
//            repositories.first { details ->
//                details.feedType() == exchange
//            }.passwordRequired()
//
//    override fun updateData(ticker: CryptoPairs, data: TradingInfo) {
//        tickerData[ticker] = data
//    }
//
//    override fun updateApiData(exchange: String, data: ApiBalances) {
//        apiData[exchange] = data
//    }
}
