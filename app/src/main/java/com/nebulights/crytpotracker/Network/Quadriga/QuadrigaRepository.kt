package com.nebulights.crytpotracker.Network.Quadriga

import android.util.Log
import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */

interface QuadrigaCallback {
    fun updateUi(ticker: CryptoTypes)
}

object QuadrigaRepository {
    private var TAG = "QuadrigaRepository"

    private lateinit var service: QuadrigaService
    private var disposables: CompositeDisposable = CompositeDisposable()
    private var tickerData: MutableMap<CryptoTypes, CurrentTradingInfo> = mutableMapOf()

    fun setService(service: QuadrigaService) {
        this.service = service
    }

    fun getCurrentTradingInfo(ticker: String): Observable<CurrentTradingInfo> {
        return service.getCurrentTradingInfo(ticker)
    }

    fun startFeed(tickers: List<CryptoTypes>, callback: QuadrigaCallback) {
        if (disposables.size() != 0) {
            disposables.clear()
        }

        tickers.forEach { ticker ->
            Log.i(TAG, ticker.ticker)
            val disposable: Disposable = getCurrentTradingInfo(ticker.ticker)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { result -> result.delay(10, TimeUnit.SECONDS) }
                    .retryWhen { error -> error.delay(10, TimeUnit.SECONDS) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        Log.d("Result", ticker.toString() + " last price is ${result.last}")
                        updateData(ticker, result)
                        callback.updateUi(ticker)
                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }
    }

    fun stopFeed() {
        disposables.clear()
    }

    fun updateData(ticker: CryptoTypes, currentTradingInfo: CurrentTradingInfo) {
        tickerData.put(ticker, currentTradingInfo)
    }

    fun getData(): Map<CryptoTypes, CurrentTradingInfo> {
        return tickerData
    }
}
