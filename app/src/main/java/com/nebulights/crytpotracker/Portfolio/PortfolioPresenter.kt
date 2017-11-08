package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.*
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaCallback
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaRepository
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.stringSafeBigDecimal

import java.math.BigDecimal

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var quadrigaRepository: QuadrigaRepository,
                         private var view: PortfolioContract.View,
                         private var tickers: List<CryptoTypes>,
                         val cryptoAssetRepository: CryptoAssetContract) : PortfolioContract.Presenter, QuadrigaCallback {

    private val TAG = "PortfolioPresenter"

    init {
        view.setPresenter(this)
    }

    override fun startFeed() {
        quadrigaRepository.startFeed(tickers, this)
    }

    override fun updateUi(ticker: CryptoTypes) {
        view.updateUi(getOrderedTickerIndex(ticker))
    }

    override fun stopFeed() {
        quadrigaRepository.stopFeed()
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

        for ((ticker, data) in quadrigaRepository.getData()) {
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

    fun tickerQuantity(ticker: CryptoTypes): BigDecimal {
        return cryptoAssetRepository.totalTickerQuantity(ticker)
    }

    fun tickerQuantityForIndex(position: Int): BigDecimal {
        return tickerQuantity(getOrderedTicker(position))
    }

    fun getCurrentTradingData(position: Int): CurrentTradingInfo? {
        return quadrigaRepository.getData()[getOrderedTicker(position)]
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    fun getOrderedTicker(position: Int): CryptoTypes {
        return tickers[position]
    }

    fun getOrderedTickerIndex(cryptoType: CryptoTypes): Int {
        return tickers.indexOf(cryptoType)
    }

    fun addTickerData(currentTradingInfo: CurrentTradingInfo, cryptoType: CryptoTypes) {
        quadrigaRepository.updateData(cryptoType, currentTradingInfo)
    }
}
