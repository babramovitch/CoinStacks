package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.NetworkErrors
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */

abstract class BaseExchange : Exchange {

    private var tickerDisposables: CompositeDisposable = CompositeDisposable()
    private var balanceDisposables: CompositeDisposable = CompositeDisposable()

    abstract val userNameRequired: Boolean
    abstract val passwordRequired: Boolean
    abstract val userNameText: String

    abstract fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): Any

    fun clearTickerDisposables() {
        if (tickerDisposables.size() != 0) {
            tickerDisposables.clear()
        }
    }

    fun clearBalanceDisposables() {
        if (balanceDisposables.size() != 0) {
            balanceDisposables.clear()
        }
    }

    fun <T> startPriceFeed(observable: Observable<T>, delay: Long, ticker: CryptoPairs, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
       observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result ->result.delay((20000 + delay), TimeUnit.MILLISECONDS) }
                .retryWhen { error -> error.delay(20000 + delay, TimeUnit.MILLISECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    result as NormalizedTickerData
                    try {
                        val tradingInfo = TradingInfo(result.lastPrice(), result.timeStamp())
                        exchangeNetworkDataUpdate.updateData(ticker, tradingInfo)
                        presenterCallback.updateUi(ticker)
                    } catch (exception: Exception) {
                        when (exception) {
                            is NullPointerException -> exchangeNetworkDataUpdate.staleDataFromError(ticker)
                            is IllegalArgumentException -> exchangeNetworkDataUpdate.staleDataFromError(ticker)
                        }
                    }
                }, { error ->
                    exchangeNetworkDataUpdate.staleDataFromError(ticker)
                    error.printStackTrace()
                }).addTo(tickerDisposables)
    }

    fun <T> startAccountBalanceFeed(observable: Observable<T>, exchange: BasicAuthentication, networkCompletionCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearBalanceDisposables()
        observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(60, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    try {
                        val balances = createNormalizedBalances(result, exchange)
                        exchangeNetworkDataUpdate.updateApiData(exchange.exchange, balances)
                        networkCompletionCallback.updateUi(balances)
                    } catch (exception: NullPointerException) {
                        exchangeNetworkDataUpdate.staleApiDataFromError(exchange.exchange)
                        networkCompletionCallback.onNetworkError(exchange.exchange, NetworkErrors.NULL)
                    }
                }, { error ->
                    if (error is HttpException) {
                        try {
                            val errorBody = error.response().errorBody()?.string()
                            networkCompletionCallback.onNetworkError(exchange.exchange, errorBody)
                        } catch (exception: Exception) {
                            networkCompletionCallback.onNetworkError(exchange.exchange, NetworkErrors.UNKNOWN)
                        }
                    } else {
                        networkCompletionCallback.onNetworkError(exchange.exchange, error.localizedMessage)
                    }
                    exchangeNetworkDataUpdate.staleApiDataFromError(exchange.exchange)
                    error.printStackTrace()
                }).addTo(balanceDisposables)
    }

    private fun <T> createNormalizedBalances(result: T, exchange: BasicAuthentication): ApiBalances {
        return if (result is Array<*>) {
            @Suppress("UNCHECKED_CAST")
            result as Array<NormalizedBalanceData>
            ApiBalances.create(exchange.exchange, exchange.getCryptoPairsMap(), result)
        } else {
            result as NormalizedBalanceData
            ApiBalances.create(exchange.exchange, exchange.getCryptoPairsMap(), result)
        }
    }

    //TODO remove the duplication of validateAPiKeys/startAccountBalanceFeed where the only real difference is the repeat

    fun <T> validateAPiKeys(observable: Observable<T>, exchange: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearBalanceDisposables()
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    try {
                        val balances = createNormalizedBalances(result, exchange)
                        exchangeNetworkDataUpdate.updateApiData(exchange.exchange, balances)
                        presenterCallback.validationSuccess()
                    } catch (exception: NullPointerException) {
                        presenterCallback.validationError(exchange.exchange, "Error Validating Credentials")
                    }
                }, { error ->
                    if (error is HttpException) {
                        try {
                            val errorBody = error.response().errorBody()?.string()
                            presenterCallback.validationError(exchange.exchange, errorBody)
                        } catch (exception: Exception) {
                            presenterCallback.validationError(exchange.exchange, "Unknown Error")
                        }
                    } else {
                        presenterCallback.validationError(exchange.exchange, error.localizedMessage)
                    }
                    error.printStackTrace()
                }).addTo(balanceDisposables)
    }

    fun totalDisposables(): Int {
        return tickerDisposables.size() + balanceDisposables.size()
    }

    override fun stopFeed() {
        tickerDisposables.clear()
        balanceDisposables.clear()
    }

    override fun userNameRequired(): Boolean = userNameRequired
    override fun passwordRequired(): Boolean = passwordRequired

}