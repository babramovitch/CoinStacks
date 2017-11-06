package com.nebulights.crytpotracker.Portfolio

import android.util.Log
import com.nebulights.crytpotracker.*
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaRepository
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.stringSafeBigDecimal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var quadrigaRepository: QuadrigaRepository,
                         private var view: PortfolioContract.View,
                         private var tickers: List<CryptoTypes>,
                         val cryptoAssetRepository: CryptoAssetRepository) : PortfolioContract.Presenter {

    private val TAG = "PortfolioPresenter"
    private var disposables: CompositeDisposable = CompositeDisposable()
    private var tickerData: MutableMap<CryptoTypes, CurrentTradingInfo> = mutableMapOf()

    init {
        view.setPresenter(this)
    }

    override fun restoreTickerData(tickerData: MutableMap<CryptoTypes, CurrentTradingInfo>) {
        this.tickerData = tickerData
    }

    override fun saveTickerDataState(): MutableMap<CryptoTypes, CurrentTradingInfo> {
        return tickerData
    }

    override fun startFeed() {
        tickers.forEach { ticker ->
            Log.i(TAG, ticker.ticker)
            val disposable: Disposable = quadrigaRepository.getTickerInfo(ticker.ticker)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { result -> result.delay(10, TimeUnit.SECONDS) }
                    .retryWhen { error -> error.delay(10, TimeUnit.SECONDS) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->

                        result.ask.notNull {
                            addTickerData(result, ticker)
                            view.updateUi(getOrderedTickerIndex(ticker))
                            Log.d("Result", ticker.toString() + " last price is ${result.last}")
                        }

                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }
    }

    override fun stopFeed() {
        disposables.dispose()
    }

    override fun showCreateAssetDialog(position: Int) {
        view.showCreateAssetDialog(getOrderedTicker(position), tickerQuantityForIndex(position).toString())
    }

    override fun createAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        createOrUpdateAsset(cryptoType, quantity, price)
        view.updateUi(getOrderedTickerIndex(cryptoType))
    }

    fun createOrUpdateAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoType, quantity, price)
    }

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {
        val currentTradingInfo = getCurrentTradingData(position)

        row.setTicker(getOrderedTicker(position).toString())

        currentTradingInfo.notNull {
            val lastPrice = stringSafeBigDecimal(currentTradingInfo!!.last)
            val holdings = tickerQuantityForIndex(position)

            row.setLastPrice(currencyFormatter().format(lastPrice))
            row.setHoldings(holdings.toString())
            row.setNetValue(currencyFormatter().format(netValue(lastPrice, holdings)))
        }
    }

    override fun clearAssets() {
        cryptoAssetRepository.clearAllData()
        view.resetUi()
    }

    override fun getNetWorth(): String {
        var netWorth = BigDecimal(0.0)

        for ((ticker, data) in tickerData) {
            netWorth += stringSafeBigDecimal(data.last) * tickerQuantity(ticker)
        }

        return currencyFormatter().format(netWorth.setScale(2, BigDecimal.ROUND_HALF_UP))
    }

    override fun tickerCount(): Int {
        return tickers.count()
    }

    override fun onDetach() {
        cryptoAssetRepository.close()
    }

    fun addTickerData(currentTradingInfo: CurrentTradingInfo, ticker: CryptoTypes) {
        if (currentTradingInfo.last.isEmpty()) {
            return
        }

        tickerData.put(ticker, currentTradingInfo)
    }

    fun tickerQuantity(ticker: CryptoTypes?): BigDecimal {
        return cryptoAssetRepository.totalTickerQuantity(ticker)
    }

    fun tickerQuantityForIndex(position: Int): BigDecimal {
        return tickerQuantity(getOrderedTicker(position))
    }

    fun getCurrentTradingData(position: Int): CurrentTradingInfo? {
        return tickerData[getOrderedTicker(position)]
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    fun getOrderedTicker(position: Int): CryptoTypes? {
        return if (position >= tickers.count() || position < 0) {
            null
        } else {
            tickers[position]
        }
    }

    fun getOrderedTickerIndex(cryptoType: CryptoTypes): Int {
        return tickers.indexOf(cryptoType)
    }
}
