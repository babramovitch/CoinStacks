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
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var realm: Realm,
                         private var quadrigaRepository: QuadrigaRepository,
                         private var viewHost: PortfolioContract.ViewHost,
                         private var view: PortfolioContract.View,
                         private var tickers: List<String>) : PortfolioContract.Presenter {

    private val TAG = "PortfolioPresenter"
    private var disposables: MutableList<Disposable> = mutableListOf()
    private var tickerData: MutableMap<String, CurrentTradingInfo> = mutableMapOf()

    init {
        view.setPresenter(this)
    }

    override fun onAttach() {
    }

    override fun onDetach() {
        disposables.forEach { disposable -> disposable.let { disposable.dispose() } }

    }

    override fun stopFeed() {
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

    override fun startFeed() {

        tickers.forEach { ticker ->
            val disposable: Disposable = quadrigaRepository.getTickerInfo(ticker)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { result -> result.delay(30, TimeUnit.SECONDS) }
                    .repeatWhen { error -> error.delay(30, TimeUnit.SECONDS) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->

                        result.ask.notNull {
                            Log.d("Result", ticker + " current asking price is ${result.last}")

                            result.ticker = ticker //maybe not needed due to map usage
                            tickerData.put(ticker, result)
                            view.updateUi(ticker, result)
                        }

                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }
    }

    override fun addAsset(asset: TrackedAsset) {
    }

    override fun getCurrentHoldings(): MutableMap<String, Double> {
        val tickerData: MutableMap<String, Double> = mutableMapOf()

        tickers.forEach { ticker ->
            tickerData.put(ticker, tickerQuantity(ticker))
        }

        return tickerData
    }

    override fun getCurrentHoldings(position: Int): Double {
        return tickerQuantity(getOrderedTicker(position))
    }

    override fun getCurrentTradingData(): MutableMap<String, CurrentTradingInfo> {
        return tickerData
    }

    override fun getCurrentTradingData(position: Int): CurrentTradingInfo? {
        return tickerData[getOrderedTicker(position)]
    }

    override fun getNetWorth(): String {
        var netWorth = 0.00

        for ((ticker, data) in tickerData) {
            netWorth += data.last!!.toDouble() * tickerQuantity(ticker)
        }

        return BigDecimal(netWorth).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
    }

    private fun tickerQuantity(ticker: String): Double {

        var value = 0.00

        when (ticker) {
            "BTC_CAD" -> value = 10.0
            "BCH_CAD" -> value = 20.0
            "ETH_CAD" -> value = 30.0
            "LTC_CAD" -> value = 40.0
        }

        return value
    }

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {
        val currentTradingInfo = getCurrentTradingData(position)

        row.setTicker(getOrderedTicker(position).replace("_", ":"))

        currentTradingInfo.notNull {
            row.setLastPrice(currentTradingInfo!!.last!!)
            row.setHoldings(getCurrentHoldings(position).toString())
        }
    }

    override fun tickerCount(): Int {
        return tickers.count()
    }

    override fun getOrderedTicker(position: Int): String {

        return when (position) {
            0 -> "BTC_CAD"
            1 -> "BCH_CAD"
            2 -> "ETH_CAD"
            3 -> "LTC_CAD"
            else -> ""
        }
    }

}
