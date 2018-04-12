package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.Network.ApiKeyValidationCallback
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetContract
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.RecordTypes
import com.nebulights.coinstacks.Types.userTicker
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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

    private var record: RecordTypes = RecordTypes.COINS

    private var disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var validator: AdditionsFormValidator

    init {
        view.setPresenter(this)
    }

    override fun getRecordType(): RecordTypes {
        return record
    }

    override fun setInitialScreenAndMode(recordType: String,
                                         exchange: String,
                                         ticker: String,
                                         editing: Boolean,
                                         exchangeList: Array<String>,
                                         validator: AdditionsFormValidator) {

        this.validator = validator
        val type = RecordTypes.valueOf(recordType)

        record = type

        showCorrectCoinTypeDetails(type)

        if (editing) {
            when (type) {
                RecordTypes.COINS -> {
                    view.setEditModeCoinsAndApi()
                }
                RecordTypes.API -> {
                    view.setEditModeCoinsAndApi()
                }
                RecordTypes.WATCH -> {
                }
            }

            updateViewsForExchangeSpinnerSelection(exchange)
            view.setExchange(exchangeList.indexOf(exchange))
            view.setCryptoPair(getTickersForExchange(exchange).indexOf(ticker))

        } else {
            view.setExchange(lastUsedExchange(exchangeList))
            updateViewsForExchangeSpinnerSelection(exchangeList[lastUsedExchange(exchangeList)])
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

    override fun updateViewsForExchangeSpinnerSelection(exchange: String) {
        val exchangeTickers = getTickersForExchange(exchange)

        when (record) {
            RecordTypes.API -> {
                view.showAuthenticationRequirements(
                        exchanges.userNameRequiredForAuthentication(exchange),
                        exchanges.passwordRequiredForAuthentication(exchange))


                view.setupApiSpinners(cryptoAssetRepository.getApiKeysNonRealmForExchange(exchange),
                        cryptosForExchange(exchange).distinct(),
                        getTickersForExchange(exchange))

                view.setAuthenticationDetails(cryptoAssetRepository.getApiKeysNonRealmForExchange(exchange))

                createFormValidator(validator.apiValidator(
                        exchanges.userNameRequiredForAuthentication(exchange),
                        exchanges.passwordRequiredForAuthentication(exchange)))
            }
            RecordTypes.COINS -> {
                view.setupCryptoPairSpinner(exchangeTickers)
                setCryptoQuantity(exchange, exchangeTickers[0])
                createFormValidator(validator.coinsValidator())
            }
            RecordTypes.WATCH -> {
                view.setupCryptoPairSpinner(exchangeTickers)
                createFormValidator(validator.watchAddressValidator())
            }
        }
    }

    override fun createFormValidator(observer: Observable<Boolean>) {
        disposable.clear()
        disposable.add(observer.subscribe { isValid ->
            if (isValid) {
                view.enableSaveButton(true)
            } else {
                view.enableSaveButton(false)
            }
        })
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


    override fun createWatchAddress(exchange: String, selectedPosition: Int, address: String) {
        val userTicker = getTickersForExchange(exchange)[selectedPosition]
        val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

        if (cryptoPair != null) {
            cryptoAssetRepository.createOrUpdateWatchAddress(cryptoPair, address)
            navigator.close()
        }
    }

    override fun createAsset(exchange: String, selectedPosition: Int, quantity: String, price: String) {
        val userTicker = getTickersForExchange(exchange)[selectedPosition]
        val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

        if (cryptoPair != null) {
            createOrUpdateAsset(cryptoPair, quantity, price)
        }
    }

    override fun createAPIKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>) {
        val basicAuthentication = BasicAuthentication(exchange, apiKey, apiSecret, apiPassword, userName, listOf())

        val startTime = System.currentTimeMillis()

        view.showVerificationDialog()

        exchanges.validateExchange(basicAuthentication, object : ApiKeyValidationCallback {
            override fun validationSuccess() {
                cryptoAssetRepository.createOrUpdateApiKey(exchange, userName, apiPassword, apiKey, apiSecret, cryptoPairs)
                val endTime = System.currentTimeMillis()
                val timeDifference = endTime - startTime
                val delay = if (timeDifference < 2000) 2000 - timeDifference else 0

                disposable.add(Observable.timer(delay, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view.closeVerificationDialog()
                        }))

                disposable.add(Observable.timer(delay + 700, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            verificationComplete()
                        }))

            }

            override fun validationError(errorBody: String?) {
                val endTime = System.currentTimeMillis()
                val timeDifference = endTime - startTime
                val delay = if (timeDifference < 3000) 3000 - timeDifference else 0

                disposable.add(Observable.timer(delay, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view.showValidationErrorDialog(errorBody)
                        }))
            }
        })
    }

    override fun verificationComplete() {
        navigator.close()
    }

    private fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)
        navigator.close()
    }

    override fun deleteRecord(exchange: String, userTicker: String) {
        when (record) {

            RecordTypes.COINS -> {
                val cryptoPair = allTickers.find { ticker ->
                    (
                            ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker)
                }

                if (cryptoPair != null) {
                    cryptoAssetRepository.removeAsset(cryptoPair)
                    close()
                }
            }
            RecordTypes.API -> {
                cryptoAssetRepository.removeApiKey(exchange)
                navigator.closeWithDeletedExchange(2, exchange)

            }
            RecordTypes.WATCH -> TODO()
        }
    }

    override fun close() {
        navigator.close()
    }

    override fun onDetach() {
        disposable.dispose()
    }
}
