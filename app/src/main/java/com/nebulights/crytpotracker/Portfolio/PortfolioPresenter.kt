package com.nebulights.crytpotracker.Portfolio

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.nebulights.crytpotracker.*
import com.nebulights.crytpotracker.Portfolio.model.CryptoAsset
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import io.realm.Realm
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var realm: Realm,
                         private var quadrigaRepository: QuadrigaRepository,
                         private var view: PortfolioContract.View,
                         private var tickers: List<CryptoTypes>) : PortfolioContract.Presenter {

    private val TAG = "PortfolioPresenter"
    private var disposables: MutableList<Disposable> = mutableListOf()
    private var tickerData: MutableMap<CryptoTypes, CurrentTradingInfo> = mutableMapOf()

    init {
        view.setPresenter(this)
    }

    override fun onAttach() {
    }

    override fun onDetach() {
        realm.close()
    }

    fun stringSafeBigDecimal(value: String): BigDecimal {
        return if (value.isNumber()) BigDecimal(value) else BigDecimal(0.00)
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

    override fun showCreateAssetDialog(position: Int) {
        view.showCreateAssetDialog(getOrderedTicker(position), getCurrentHoldings(position).toString())
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
                            view.updateUi(getOrderedTicker(ticker))
                            Log.d("Result", ticker.toString() + " current asking price is ${result.last}")
                        }

                    }, { error ->
                        error.printStackTrace()
                    })

            disposables.add(disposable)
        }
    }

    fun addTickerData(currentTradingInfo: CurrentTradingInfo, ticker: CryptoTypes) {
        if (currentTradingInfo.last == null) {
            return
        }

        tickerData.put(ticker, currentTradingInfo)
    }

    override fun createAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        createOrUpdateAsset(cryptoType, quantity, price)
        view.updateUi(getOrderedTicker(cryptoType))
    }

    fun createOrUpdateAsset(cryptoType: CryptoTypes, quantity: String, price: String) {

        val asset = realm.where(CryptoAsset::class.java).equalTo("type", cryptoType.toString()).findFirst()

        if (asset == null) {
            realm.executeTransaction {
                val newAsset = realm.createObject(CryptoAsset::class.java)
                newAsset.setAmount(stringSafeBigDecimal(quantity))
                newAsset.setPurchasePrice(stringSafeBigDecimal(price))
                newAsset.setCurrency(CurrencyTypes.CAD)
                newAsset.setCrytpoType(cryptoType)
            }
        } else {
            realm.executeTransaction {
                asset.setAmount(stringSafeBigDecimal(quantity))
                asset.setPurchasePrice(stringSafeBigDecimal(price))
            }
        }
    }

    override fun clearAssets() {
        realm.executeTransaction {
            realm.deleteAll()
        }
        view.resetUi()
    }

    override fun getCurrentHoldings(): MutableMap<CryptoTypes, BigDecimal> {
        val tickerData: MutableMap<CryptoTypes, BigDecimal> = mutableMapOf()

        tickers.forEach { ticker ->
            tickerData.put(ticker, tickerQuantity(ticker))
        }

        return tickerData
    }

    override fun getCurrentHoldings(position: Int): BigDecimal {
        val ticker = getOrderedTicker(position)

        return if (ticker != null) {
            tickerQuantity(ticker)
        } else {
            BigDecimal("0.0")
        }
    }

    override fun getCurrentTradingData(): MutableMap<CryptoTypes, CurrentTradingInfo> {
        return tickerData
    }

    override fun getCurrentTradingData(position: Int): CurrentTradingInfo? {
        return tickerData[getOrderedTicker(position)]
    }

    override fun getNetWorth(): String {
        var netWorth = BigDecimal(0.0)

        for ((ticker, data) in tickerData) {
            netWorth += stringSafeBigDecimal(data.last!!) * tickerQuantity(ticker)
        }

        val formatter = DecimalFormat("$###,###,##0.00")

        return formatter.format(netWorth.setScale(2, BigDecimal.ROUND_HALF_UP))
    }

    fun tickerQuantity(ticker: CryptoTypes): BigDecimal {

        var total: BigDecimal = BigDecimal.valueOf(0.0)

        val assets = realm.where(CryptoAsset::class.java).equalTo("type", ticker.toString()).findAll()
        assets.forEach { asset -> total += asset.getAmount() }

        return total
    }

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {
        val currentTradingInfo = getCurrentTradingData(position)

        row.setTicker(getOrderedTicker(position).toString())

        currentTradingInfo.notNull {
            val lastPrice = stringSafeBigDecimal(currentTradingInfo!!.last!!)
            val holdings = getCurrentHoldings(position)

            val formatter = DecimalFormat("$###,###,##0.00")

            row.setLastPrice(formatter.format(lastPrice))
            row.setHoldings(holdings.toString())
            row.setNetValue(formatter.format(netValue(lastPrice, holdings)))
        }
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)

    }

    override fun tickerCount(): Int {
        return tickers.count()
    }

    override fun getOrderedTicker(position: Int): CryptoTypes? {
        return if (position >= tickers.count() || position < 0) {
            null
        } else {
            tickers[position]
        }
    }

    fun getOrderedTicker(cryptoType: CryptoTypes): Int {
        return tickers.indexOf(cryptoType)
    }

    fun formatCurrency(amount: String): String {
        val formatter = DecimalFormat("$###,###,##0.00")
        return formatter.format(amount)
    }
}
