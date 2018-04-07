package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.Network.ApiKeyValidationCallback
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetContract
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.RecordTypes
import com.nebulights.coinstacks.Types.userTicker

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

    var record: RecordTypes = RecordTypes.COINS

    init {
        view.setPresenter(this)
    }

    override fun getRecordType(): RecordTypes {
        return record
    }

    override fun setInitialScreenAndMode(recordType: String, exchange: String, ticker: String, editing: Boolean, exchangeList: Array<String>) {

        val type = RecordTypes.valueOf(recordType)

        record = type

        showCorrectCoinTypeDetails(type)

        if (editing) {
            when (type) {
                RecordTypes.COINS -> {
                    view.setEditModeCoinsAndApi()
                    setCryptoQuantity(exchange, ticker)
                }
                RecordTypes.API -> {
                    view.setEditModeCoinsAndApi()
                }
                RecordTypes.WATCH -> {
                }
            }

            updateExchangeSpinnerSelection(exchange)
            view.setExchange(exchangeList.indexOf(exchange))
            view.setCryptoPair(getTickersForExchange(exchange).indexOf(ticker))


        } else {
            view.setExchange(lastUsedExchange(exchangeList))
            updateExchangeSpinnerSelection(exchangeList[lastUsedExchange(exchangeList)])
        }
    }

    override fun setCryptoQuantity(exchange: String, ticker: String) {
        val cryptoPair = allTickers.find { tickers -> (tickers.exchange.toLowerCase() == exchange.toLowerCase() && tickers.userTicker() == ticker) }
        if (cryptoPair != null) {
            val quantity = cryptoAssetRepository.totalTickerQuantity(cryptoPair).toString()
            if (quantity != "0.0") {
                view.setCryptoQuantity(cryptoAssetRepository.totalTickerQuantity(cryptoPair).toString())
            } else {
                view.setCryptoQuantity("")
            }
        }
    }

    override fun showCorrectCoinTypeDetails(recordType: RecordTypes) = when (recordType) {
        RecordTypes.COINS -> view.showCoinAddition()
        RecordTypes.API -> view.showAPIAddition()
        RecordTypes.WATCH -> view.showWatchAddition()
    }

    override fun updateExchangeSpinnerSelection(exchange: String) {

        val exchangeTickers = getTickersForExchange(exchange)
        view.setupCryptoPairSpinner(exchangeTickers)
        setCryptoQuantity(exchange, exchangeTickers[0])

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
        //TODO validate the data before continuing

        val basicAuthentication = BasicAuthentication(exchange, apiKey, apiSecret, apiPassword, userName, listOf())

        exchanges.validateExchange(basicAuthentication, object : ApiKeyValidationCallback {
            override fun validationSuccess() {
                //close loading dialog
                cryptoAssetRepository.createOrUpdateApiKey(exchange, userName, apiPassword, apiKey, apiSecret, cryptoPairs)
                navigator.close()
            }

            override fun validationError(errorBody: String?) {
                //show error message
            }

        })


    }


    private fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)
        navigator.close()
    }

    override fun deleteRecord(exchange: String, userTicker: String) {

        when (record) {

            RecordTypes.COINS -> {
                val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

                if (cryptoPair != null) {
                    cryptoAssetRepository.removeAsset(cryptoPair)
                    navigator.close()
                }
            }
            RecordTypes.API -> {
                cryptoAssetRepository.removeApiKey(exchange)
                navigator.closeWithDeletedExchange(2, exchange)

            }
            RecordTypes.WATCH -> TODO()
        }


    }

    override fun onDetach() {
    }

}
