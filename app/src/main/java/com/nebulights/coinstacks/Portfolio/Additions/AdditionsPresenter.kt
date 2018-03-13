package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.*
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetContract

/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsPresenter(
        private var view: AdditionsContract.View,
        private var exchanges: Exchanges,
        private val cryptoAssetRepository: CryptoAssetContract,
        private var navigator: AdditionsContract.Navigator) : AdditionsContract.Presenter {


    private val TAG = "AdditionsPresenter"
    private var allTickers = enumValues<CryptoPairs>().map { it }

    init {
        view.setPresenter(this)
    }

    override fun showCorrectCoinTypeDetails(id: Int) {
        when (id) {
            0 -> view.showCoinAddition()
            1 -> view.showAPIAddition()
            2 -> view.showWatchAddition()
        }
    }

    override fun updateExchangeSpinnerSelection(exchange: String) {

        view.setupCryptoPairSpinner(getTickersForExchange(exchange))

        view.showAuthenticationRequirements(
                exchanges.userNameRequiredForAuthentication(exchange),
                exchanges.passwordRequiredForAuthentication(exchange))


        view.setupApiSpinners(cryptoAssetRepository.getApiKeysNonRealmForExchange(exchange),
                cryptosForExchange(exchange).distinct(),
                getTickersForExchange(exchange))

        view.setAuthenticationDetails(cryptoAssetRepository.getApiKeysNonRealmForExchange(exchange))

    }

    override fun lastUsedExchange(exchanges: Array<String>): Int {
        val exchange = cryptoAssetRepository.lastUsedExchange()
        val exchangeIndex = exchanges.indexOf(exchange)

        return if (exchangeIndex == -1) {
            0
        } else {
            exchangeIndex
        }
    }

    override fun getTickersForExchange(exchange: String): List<String> {
        return allTickers.filter { ticker ->
            ticker.exchange.toLowerCase() == exchange.toLowerCase()
        }.map { ticker -> ticker.userTicker() }
    }

    override fun getTickerForExchangeAndPair(exchange: String, pair: String): List<CryptoPairs> =
            allTickers.filter { ticker ->
                ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == pair
            }

    override fun cryptosForExchange(exchange: String): List<CryptoTypes> =
            allTickers.filter { ticker ->
                ticker.exchange.toLowerCase() == exchange.toLowerCase()
            }.map { ticker -> ticker.cryptoType }

    override fun createAsset(exchange: String, selectedPosition: Int, quantity: String, price: String) {
        val userTicker = getTickersForExchange(exchange)[selectedPosition]
        val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

        if (cryptoPair != null) {
            createOrUpdateAsset(cryptoPair, quantity, price)
        }
    }

    override fun createAPIKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>) {
        cryptoAssetRepository.createOrUpdateApiKey(exchange, userName, apiPassword, apiKey, apiSecret, cryptoPairs)

        //TODO validate the data before continuing
        navigator.close()
    }

    private fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)
        navigator.close()
    }

    override fun onDetach() {
    }

}
