package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs
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

    fun <T> startPriceFeed(observable: Observable<T>, ticker: CryptoPairs, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(20, TimeUnit.SECONDS) }
                .retryWhen { error -> error.delay(20, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    result as NormalizedTickerData
                    val tradingInfo = TradingInfo(result.lastPrice(), result.timeStamp())
                    exchangeNetworkDataUpdate.updateData(ticker, tradingInfo)
                    presenterCallback.updateUi(ticker)
                }, { error ->
                    error.printStackTrace()
                }).addTo(tickerDisposables)
    }

    fun <T> startAccountBalanceFeed(observable: Observable<T>, exchange: BasicAuthentication, networkCompletionCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearBalanceDisposables()
        observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(15, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val balances = createNormalizedBalances(result, exchange)
                    exchangeNetworkDataUpdate.updateApiData(exchange.exchange, balances)
                    networkCompletionCallback.updateUi(balances)
                }, { error ->
                    error.printStackTrace()
                }).addTo(balanceDisposables)
    }

    //TODO remove the duplication of validateAPiKeys/startAccountBalanceFeed where the only real difference is the repeat

    fun <T> validateAPiKeys(observable: Observable<T>, exchange: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearBalanceDisposables()
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val balances = createNormalizedBalances(result, exchange)
                    exchangeNetworkDataUpdate.updateApiData(exchange.exchange, balances)
                    presenterCallback.validationSuccess()
                }, { error ->
                    error as HttpException
                    val errorBody = error.response().errorBody()?.string()
                    error.printStackTrace()
                    presenterCallback.validationError(errorBody)
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

    override fun stopFeed() {
        tickerDisposables.clear()
        balanceDisposables.clear()
    }

    override fun userNameRequired(): Boolean = userNameRequired
    override fun passwordRequired(): Boolean = passwordRequired

}