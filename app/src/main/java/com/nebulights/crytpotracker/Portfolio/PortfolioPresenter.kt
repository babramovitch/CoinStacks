package com.nebulights.crytpotracker.Portfolio

import android.os.Bundle
import android.util.Log
import com.nebulights.crytpotracker.*
import com.nebulights.crytpotracker.Portfolio.model.CryptoAsset
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaRepository
import com.squareup.moshi.Types
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import io.realm.Realm
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var realm: Realm,
                         private var quadrigaRepository: QuadrigaRepository,
                         private var view: PortfolioContract.View,
                         private var tickers: List<CryptoTypes>,
                         private val moshi: Moshi) : PortfolioContract.Presenter {

    private val TAG = "PortfolioPresenter"
    private var disposables: MutableList<Disposable> = mutableListOf()
    private var tickerData: MutableMap<CryptoTypes, CurrentTradingInfo> = mutableMapOf()

    init {
        view.setPresenter(this)
    }

    override fun restoreTickerData(presenterState: Bundle) {
        val tickerBundleData = presenterState.getString("PRESENTER_TICKER_DATA")

        if (tickerBundleData.isNotEmpty()) {
            tickerData = tickerDataJsonAdapter().fromJson(tickerBundleData)
        }
    }

    override fun saveTickerDataState(): String {
        return tickerDataJsonAdapter().toJson(tickerData)
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

    override fun stopFeed() {
        disposables.forEach { disposable ->
            disposable.let {
                disposable.dispose()
            }
        }
    }

    override fun showCreateAssetDialog(position: Int) {
        view.showCreateAssetDialog(getOrderedTicker(position), getCurrentHoldings(position).toString())
    }

    override fun createAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        createOrUpdateAsset(cryptoType, quantity, price)
        view.updateUi(getOrderedTicker(cryptoType))
    }

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {
        val currentTradingInfo = getCurrentTradingData(position)

        row.setTicker(getOrderedTicker(position).toString())

        currentTradingInfo.notNull {
            val lastPrice = stringSafeBigDecimal(currentTradingInfo!!.last!!)
            val holdings = getCurrentHoldings(position)

            row.setLastPrice(currencyFormatter().format(lastPrice))
            row.setHoldings(holdings.toString())
            row.setNetValue(currencyFormatter().format(netValue(lastPrice, holdings)))
        }
    }

    override fun clearAssets() {
        realm.executeTransaction {
            realm.deleteAll()
        }
        view.resetUi()
    }

    override fun getNetWorth(): String {
        var netWorth = BigDecimal(0.0)

        for ((ticker, data) in tickerData) {
            netWorth += stringSafeBigDecimal(data.last!!) * tickerQuantity(ticker)
        }

        return currencyFormatter().format(netWorth.setScale(2, BigDecimal.ROUND_HALF_UP))
    }

    override fun tickerCount(): Int {
        return tickers.count()
    }

    override fun onDetach() {
        realm.close()
    }

    fun stringSafeBigDecimal(value: String): BigDecimal {
        return if (value.isNumber()) BigDecimal(value) else BigDecimal(0.00)
    }

    fun tickerDataJsonAdapter(): JsonAdapter<MutableMap<CryptoTypes, CurrentTradingInfo>> {
        val type = Types.newParameterizedType(MutableMap::class.java, CryptoTypes::class.java, CurrentTradingInfo::class.java)
        return moshi.adapter<MutableMap<CryptoTypes, CurrentTradingInfo>>(type)
    }

    fun addTickerData(currentTradingInfo: CurrentTradingInfo, ticker: CryptoTypes) {
        if (currentTradingInfo.last == null) {
            return
        }

        tickerData.put(ticker, currentTradingInfo)
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

    fun getCurrentHoldings(position: Int): BigDecimal {
        val ticker = getOrderedTicker(position)

        return if (ticker != null) {
            tickerQuantity(ticker)
        } else {
            BigDecimal("0.0")
        }
    }

    fun getCurrentTradingData(position: Int): CurrentTradingInfo? {
        return tickerData[getOrderedTicker(position)]
    }

    fun tickerQuantity(ticker: CryptoTypes): BigDecimal {

        var total: BigDecimal = BigDecimal.valueOf(0.0)

        val assets = realm.where(CryptoAsset::class.java).equalTo("type", ticker.toString()).findAll()
        assets.forEach { asset -> total += asset.getAmount() }

        return total
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)

    }

    fun getOrderedTicker(position: Int): CryptoTypes? {
        return if (position >= tickers.count() || position < 0) {
            Log.i(TAG, "A")
            null
        } else {
            Log.i(TAG, "B")
            tickers[position]
        }
    }

    fun getOrderedTicker(cryptoType: CryptoTypes): Int {
        return tickers.indexOf(cryptoType)
    }

    private fun currencyFormatter(): DecimalFormat {
        return DecimalFormat("$###,###,##0.00")

    }


}
