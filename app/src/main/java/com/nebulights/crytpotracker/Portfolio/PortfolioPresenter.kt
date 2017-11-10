package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.*
import com.nebulights.crytpotracker.Network.Exchanges
import com.nebulights.crytpotracker.Network.NetworkCompletionCallback
import com.nebulights.crytpotracker.Network.exchanges.TradingInfo
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.stringSafeBigDecimal

import java.math.BigDecimal

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var exchanges: Exchanges,
                         private var view: PortfolioContract.View,
                         val cryptoAssetRepository: CryptoAssetContract) : PortfolioContract.Presenter, NetworkCompletionCallback {

    private val TAG = "PortfolioPresenter"
    private var allTickers = enumValues<CryptoPairs>().map { it }
    private var tickers: MutableList<CryptoPairs>

    init {
        view.setPresenter(this)
        tickers = cryptoAssetRepository.getTickers()
    }

    override fun startFeed() {
        exchanges.startFeed(tickers, this)
    }

    override fun updateUi(ticker: CryptoPairs) {
        view.updateUi(getOrderedTickerIndex(ticker))
    }

    override fun stopFeed() {
        exchanges.stopFeed()
    }

    override fun showAddNewAssetDialog() {
        view.showAddNewAssetDialog()
    }

    override fun showCreateAssetDialog(position: Int) {
        view.showCreateAssetDialog(getOrderedTicker(position), tickerQuantityForIndex(position).toString())
    }

    override fun createAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        createOrUpdateAsset(cryptoPair, quantity, price)
        view.updateUi(getOrderedTickerIndex(cryptoPair))
    }

    override fun createAsset(exchange: String, userTicker: String, quantity: String, price: String) {
        val cryptoPair = allTickers.find { ticker -> (ticker.exchange.toLowerCase() == exchange.toLowerCase() && ticker.userTicker() == userTicker) }

        createOrUpdateAsset(cryptoPair!!, quantity, price)
        updateUi(cryptoPair)

        exchanges.startFeed(tickers, this)
    }

    fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)

        if (tickers.indexOf(cryptoPair) == -1) {
            tickers.add(cryptoPair)
        }
    }

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {
        val currentTradingInfo = getCurrentTradingData(position)

        val ticker = getOrderedTicker(position)

        row.setTicker(ticker.userTicker())
        row.setExchange(ticker.exchange)

        val holdings = tickerQuantityForIndex(position)
        row.setHoldings(holdings.toString())

        if (currentTradingInfo != null) {
            val lastPrice = stringSafeBigDecimal(currentTradingInfo.lastPrice)
            row.setLastPrice(currencyFormatter().format(lastPrice))
            row.setHoldings(holdings.toString())
            row.setNetValue(currencyFormatter().format(netValue(lastPrice, holdings)))
        } else {
            row.setLastPrice("---")
            row.setNetValue("---")
        }
    }

    override fun clearAssets() {
        cryptoAssetRepository.clearAllData()
        view.resetUi()
    }

    override fun getNetWorth(): String {
        var netWorth = BigDecimal(0.0)

        for ((ticker, data) in exchanges.getData()) {
            netWorth += stringSafeBigDecimal(data.lastPrice) * tickerQuantity(ticker)
        }

        return currencyFormatter().format(netWorth.setScale(2, BigDecimal.ROUND_HALF_UP))
    }

    override fun tickerCount(): Int {
        return tickers.count()
    }

    override fun onDetach() {
        cryptoAssetRepository.close()
    }

    fun tickerQuantity(ticker: CryptoPairs): BigDecimal {
        return cryptoAssetRepository.totalTickerQuantity(ticker)
    }

    fun tickerQuantityForIndex(position: Int): BigDecimal {
        return tickerQuantity(getOrderedTicker(position))
    }

    fun getCurrentTradingData(position: Int): TradingInfo? {
        return exchanges.getData()[getOrderedTicker(position)]
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    fun getOrderedTicker(position: Int): CryptoPairs {
        return tickers[position]
    }

    fun getOrderedTickerIndex(cryptoPair: CryptoPairs): Int {
        return tickers.indexOf(cryptoPair)
    }

    fun addTickerData(tradingInfo: TradingInfo, cryptoPair: CryptoPairs) {
        exchanges.updateData(cryptoPair, tradingInfo)
    }

    override fun getTickers(): List<CryptoPairs> {
        return tickers
    }

    override fun getTickersForExchange(exchange: String): List<String> {
        return allTickers.filter { ticker ->
            ticker.exchange.toLowerCase() == exchange.toLowerCase()
        }.map { ticker -> ticker.userTicker() }
    }

    override fun removeAsset(cryptoPair: CryptoPairs) {
        cryptoAssetRepository.removeAsset(cryptoPair)
        val position = tickers.indexOf(cryptoPair)
        tickers.remove(cryptoPair)
        view.removeItem(position)

        if (tickers.isNotEmpty()) {
            startFeed()
        } else {
            stopFeed()
        }
    }
}
