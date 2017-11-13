package com.nebulights.coinstacks.Network.exchanges

import android.util.Log
import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.NormalizedTickerData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */

abstract class BaseExchange : Exchange {

    var disposables: CompositeDisposable = CompositeDisposable()

    fun clearDisposables() {
        if (disposables.size() != 0) {
            disposables.clear()
        }
    }

    fun <T> startFeed(observable: Observable<T>, ticker: CryptoPairs, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
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

        disposables.add(disposable)
    }

    override fun stopFeed() {
        disposables.clear()
    }
}