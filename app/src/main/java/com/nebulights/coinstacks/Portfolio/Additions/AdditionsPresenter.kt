package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.Common.ConnectionChecker
import com.nebulights.coinstacks.Network.BlockExplorers.Explorers
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.Exchanges
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetContract
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.RecordTypes
import com.nebulights.coinstacks.Types.userTicker
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsPresenter(
        private var view: AdditionsContract.View,
        private var exchanges: Exchanges,
        private var explorers: Explorers,
        private val cryptoAssetRepository: CryptoAssetContract,
        private var navigator: AdditionsContract.Navigator,
        private var connectionChecker: ConnectionChecker) : AdditionsContract.Presenter {


    private val TAG = "AdditionsPresenter"
    private var allTickers = enumValues<CryptoPairs>().map { it }

    private var record: RecordTypes = RecordTypes.COINS
    private var editing: Boolean = false
    private var originalAddressForEditing: String = ""

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
                                         address: String,
                                         editing: Boolean,
                                         exchangeList: Array<String>,
                                         validator: AdditionsFormValidator) {

        this.validator = validator
        val type = RecordTypes.valueOf(recordType)

        this.editing = editing
        originalAddressForEditing = address
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
                    val watchAddress = cryptoAssetRepository.getWatchAddress(address)
                    if (watchAddress != null) {
                        view.setEditModeWatch(watchAddress)
                    }
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
        var exchangeTickers = getTickersForExchange(exchange)

        when (record) {
            RecordTypes.API -> {
                view.showAuthenticationRequirements(
                        exchanges.userNameRequiredForAuthentication(exchange),
                        exchanges.passwordRequiredForAuthentication(exchange),
                        exchanges.userNameText(exchange))


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
                exchangeTickers = exchangeTickers.filterNot { it.contains(CryptoTypes.XMR.name) }
                view.setupCryptoPairSpinner(exchangeTickers)
                setExplorerFromUserTicker(exchangeTickers[0])

                createFormValidator(validator.watchAddressValidator())
            }
        }
    }

    override fun setExplorerFromUserTicker(userTicker: String){
        val firstTicker = userTicker.split(" : ")
        val type = CryptoTypes.valueOf(firstTicker[0])
        view.setExplorer(type.explorerWebsite, firstTicker[0])
    }

    override fun createFormValidator(observer: Observable<Boolean>) {
        disposable.clear()
        observer.subscribe { isValid ->
            if (isValid) {
                view.enableSaveButton(true)
            } else {
                view.enableSaveButton(false)
            }
        }.addTo(disposable)
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


    override fun createWatchAddress(exchange: String, selectedPosition: Int, address: String, nickName: String) {

        if(!connectionChecker.isInternetAvailable()){
            view.showValidationErrorDialog(connectionChecker.noInternetMessage)
            return
        }

        val userTicker = getTickersForExchange(exchange)[selectedPosition]
        val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

        val startTime = System.currentTimeMillis()

        view.showVerificationDialog()

        if (cryptoPair != null) {
            explorers.validateWatchAddress(address, cryptoPair.cryptoType, object : ValidationCallback {
                override fun validationSuccess() {
                    if (editing) {
                        cryptoAssetRepository.updateWatchAddress(cryptoPair, address, originalAddressForEditing, nickName)
                    } else {
                        cryptoAssetRepository.createWatchAddress(cryptoPair, address, nickName)
                    }

                    friendlyAnimationDelaysForValidationSuccess(startTime)
                }

                override fun validationError(exchange: String, errorBody: String?) {
                    friendlyAnimationDelaysForValidationError(startTime, errorBody)
                }
            })
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

        if(!connectionChecker.isInternetAvailable()){
            view.showValidationErrorDialog(connectionChecker.noInternetMessage)
            return
        }

        val basicAuthentication = BasicAuthentication(exchange, apiKey, apiSecret, apiPassword, userName, listOf())

        val startTime = System.currentTimeMillis()

        view.showVerificationDialog()

        exchanges.validateExchange(basicAuthentication, object : ValidationCallback {
            override fun validationSuccess() {
                cryptoAssetRepository.createOrUpdateApiKey(exchange, userName, apiPassword, apiKey, apiSecret, cryptoPairs)
                friendlyAnimationDelaysForValidationSuccess(startTime)
            }

            override fun validationError(exchange: String, errorBody: String?) {
                friendlyAnimationDelaysForValidationError(startTime, errorBody)
            }
        })
    }

    fun friendlyAnimationDelaysForValidationSuccess(startTime: Long) {
        val endTime = System.currentTimeMillis()
        val timeDifference = endTime - startTime
        val delay = if (timeDifference < 2000) 2000 - timeDifference else 0

        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.closeVerificationDialog()
                }).addTo(disposable)

        Observable.timer(delay + 700, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    verificationComplete()
                }).addTo(disposable)
    }

    fun friendlyAnimationDelaysForValidationError(startTime: Long, error: String?) {
        val endTime = System.currentTimeMillis()
        val timeDifference = endTime - startTime
        val delay = if (timeDifference < 3000) 3000 - timeDifference else 0

        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showValidationErrorDialog(error)
                }).addTo(disposable)
    }

    override fun verificationComplete() {
        navigator.close()
    }

    private fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)
        navigator.close()
    }

    override fun deleteRecord(exchange: String, userTicker: String, address: String) {
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
            RecordTypes.WATCH -> {
                cryptoAssetRepository.removeWatchAddress(address)
                navigator.close()
            }
        }
    }

    override fun close() {
        navigator.close()
    }

    override fun onDetach() {
        disposable.dispose()
    }
}
