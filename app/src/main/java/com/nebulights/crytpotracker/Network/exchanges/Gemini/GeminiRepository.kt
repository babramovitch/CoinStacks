package com.nebulights.crytpotracker.Network.exchanges.Gemini

import android.util.Log
import com.nebulights.crytpotracker.CryptoPairs
import com.nebulights.crytpotracker.Network.NetworkCompletionCallback
import com.nebulights.crytpotracker.Network.exchanges.Gemini.model.CurrentTradingInfo
import com.nebulights.crytpotracker.Network.NetworkDataUpdate
import com.nebulights.crytpotracker.Network.Exchange
import com.nebulights.crytpotracker.Network.exchanges.TradingInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */


class GeminiRepository(val service: GeminiService) : Exchange {
    private var TAG = "QuadrigaRepository"
    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun feedType(): String {
        return CryptoPairs.GEMINI_BTC_USD.exchange
    }

    fun getCurrentTradingInfo(ticker: String): Observable<CurrentTradingInfo> {
        return service.getCurrentTradingInfo(ticker)
    }

    override fun startFeed(tickers: List<CryptoPairs>, callback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
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
                        Log.d("Result", ticker.toString() + "last price is ${result.lastPrice()}")

                        val tradingInfo = TradingInfo(result.lastPrice(), result.timeStamp())
                        networkDataUpdate.updateData(ticker, tradingInfo)
                        callback.updateUi(ticker)

                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }
    }

    override fun stopFeed() {
        disposables.clear()
    }
}
