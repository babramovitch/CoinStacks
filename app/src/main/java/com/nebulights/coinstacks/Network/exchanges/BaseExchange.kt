package com.nebulights.coinstacks.Network.exchanges

import android.util.Log
import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    fun <T> startPriceFeed(observable: Observable<T>, ticker: CryptoPairs, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        val disposable = observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(10, TimeUnit.SECONDS) }
                .retryWhen { error -> error.delay(10, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    result as NormalizedTickerData

                    Log.d("Result", ticker.toString() + "last price is ${result.lastPrice()}")

                    val tradingInfo = TradingInfo(result.lastPrice(), result.timeStamp())
                    networkDataUpdate.updateData(ticker, tradingInfo)
                    presenterCallback.updateUi(ticker)

                }, { error ->
                    error.printStackTrace()
                })

        tickerDisposables.add(disposable)
    }

    fun <T> startAccountBalanceFeed(observable: Observable<T>, exchange: BasicAuthentication, networkCompletionCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearBalanceDisposables()
        val disposable = observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(15, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->

                    val balances: ApiBalances

                    if (result is Array<*>) {
                        @Suppress("UNCHECKED_CAST")
                        result as Array<NormalizedBalanceData>
                        balances = ApiBalances.create(exchange.exchange, exchange.getCryptoPairsMap(), result)
                        Log.i("ASDF", balances.toString())
                    } else {
                        result as NormalizedBalanceData
                        balances = ApiBalances.create(exchange.exchange, exchange.getCryptoPairsMap(), result)
                        Log.i("ASDF", balances.toString())
                    }

                    networkDataUpdate.updateApiData(exchange.exchange, balances)
                    networkCompletionCallback.updateUi(balances)

                }, { error ->
                    error.printStackTrace()
                })

        balanceDisposables.add(disposable)
    }


    override fun stopFeed() {
        tickerDisposables.clear()
        balanceDisposables.clear()
    }

    override fun userNameRequired(): Boolean = userNameRequired
    override fun passwordRequired(): Boolean = passwordRequired

}