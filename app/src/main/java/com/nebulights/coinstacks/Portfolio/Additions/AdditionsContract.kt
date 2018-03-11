package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.BasicAuthenticationRealm


/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsContract {

    interface View {
        fun setPresenter(presenter: Presenter)
        fun showCoinAddition()
        fun showAPIAddition()
        fun showWatchAddition()
        fun showAuthenticationRequirements(userName: Boolean, password: Boolean)
        fun setAuthenticationDetails(basicAuthentication: BasicAuthentication)
        fun setupApiSpinners(basicAuthentication: BasicAuthentication, cryptosForExchange: List<CryptoTypes>, cryptoList: List<String>)
        fun setupCryptoPairSpinner(cryptoList: List<String>)
    }

    interface Presenter {
        fun onDetach()
        fun showCorrectCoinTypeDetails(id: Int)
        fun lastUsedExchange(exchanges: Array<String>): Int
        fun getTickersForExchange(exchange: String): List<String>
        fun createAsset(exchange: String, userTicker: String, quantity: String, price: String)
        fun cryptosForExchange(exchange: String): List<CryptoTypes>
        fun getTickerForExchangeAndPair(exchange: String, pair: String): List<CryptoPairs>
        fun createAPIKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>)
        fun updateExchangeSpinnerSelection(exchange: String)
    }

    interface Navigator {
        fun close()
    }

}

