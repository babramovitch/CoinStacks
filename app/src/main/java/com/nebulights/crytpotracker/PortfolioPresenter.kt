package com.nebulights.crytpotracker

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.nebulights.crytpotracker.Quadriga.CurrentTradingInfo
import com.nebulights.crytpotracker.Quadriga.QuadrigaRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import io.realm.Realm
import java.util.concurrent.TimeUnit


/**
 * Created by babramovitch on 10/23/2017.
 */


class PortfolioPresenter(private var realm: Realm,
                         private var quadrigaRepository: QuadrigaRepository,
                         private var viewHost: PortfolioContract.ViewHost,
                         private var view: PortfolioContract.View) : PortfolioContract.Presenter {

    private val TAG = "PortfolioPresenter"
    var disposables: MutableList<Disposable> = mutableListOf<Disposable>()
    var tickerData: MutableMap<String, CurrentTradingInfo> = mutableMapOf<String, CurrentTradingInfo>()


    init {
        view.setPresenter(this)
    }

    fun onDestroy() {
//        this.viewHost = null
//        this.view = null

        disposables.forEach { disposable -> disposable.let { disposable.dispose() } }

    }

    override fun stop() {
//        this.viewHost = null
//        this.view = null

        disposables.forEach { disposable ->
            disposable.let {
                disposable.dispose()
            }
        }
    }

    fun restoreState(presenterState: Bundle) {
        //Do Restore
    }

    fun saveState(): Parcelable? {
        return null
    }

    override fun dosomething(tickers: List<String>) {
        //  Log.i(TAG, "Reporting on Tickers: " + tickers.toString())

        tickers.forEach { ticker ->
            val disposable: Disposable = quadrigaRepository.getTickerInfo(ticker)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { result -> result.delay(5000, TimeUnit.MILLISECONDS) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->

                        result.ask.notNull {
                            Log.d("Result", ticker + " current asking price is ${result.last}")
                            tickerData.put(ticker, result)
                            //viewHost.blahblah()
                            view.updateUi(ticker, result)
                        }

                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }

    }

    override fun loadTradingData() {

    }

    override fun loadPortfolioData() {

    }

    override fun addAsset(asset: TrackedAsset) {

    }

}
