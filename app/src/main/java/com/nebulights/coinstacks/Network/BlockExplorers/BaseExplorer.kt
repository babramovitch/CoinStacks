package com.nebulights.coinstacks.Network.BlockExplorers

import android.util.Log
import com.nebulights.coinstacks.Network.ApiKeyValidationCallback
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Types.CryptoPairs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.concurrent.TimeUnit


/**
 * Created by babramovitch on 10/25/2017.
 */

abstract class BaseExplorer : Explorer {

    private var tickerDisposables: CompositeDisposable = CompositeDisposable()
    private var balanceDisposables: CompositeDisposable = CompositeDisposable()


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

    fun <T> startFeed(observable: Observable<T>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        val disposable = observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(60, TimeUnit.SECONDS) }
                //  .retryWhen { error -> error.delay(10, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.i("TAG", "WE GOT BALANCES " + result)
                }, { error ->
                    error.printStackTrace()
                })

        tickerDisposables.add(disposable)
    }


    override fun stopFeed() {
        tickerDisposables.clear()
        balanceDisposables.clear()
    }

}