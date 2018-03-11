package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.*
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.TradingInfo
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.smallCurrencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal

import java.math.BigDecimal

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var exchanges: Exchanges,
                         private var view: PortfolioContract.View,
                         private val cryptoAssetRepository: CryptoAssetContract,
                         private val navigation: PortfolioContract.Navigator) :
        PortfolioContract.Presenter, NetworkCompletionCallback {

    private val TAG = "PortfolioPresenter"
    private var allTickers = enumValues<CryptoPairs>().map { it }
    private var tickers: MutableList<CryptoPairs>
    private var apiKeys: MutableList<BasicAuthentication>
    private var testList: MutableList<Any> = mutableListOf()

    private var timeStopped = System.currentTimeMillis()
    private val MINUTE_IN_MILLIS = 60000

    init {
        view.setPresenter(this)
        tickers = cryptoAssetRepository.getTickers()
        apiKeys = cryptoAssetRepository.getApiKeysNonRealm()

        tickers.forEach { testList.add(it) }
        apiKeys.forEach { testList.add(it) }

    }

    fun refreshData() {
        tickers = cryptoAssetRepository.getTickers()
        apiKeys = cryptoAssetRepository.getApiKeysNonRealm()
        testList.clear()

        tickers.forEach { testList.add(it) }
        apiKeys.forEach { testList.add(it) }

    }

    override fun startFeed() {
        if (shouldSecureData(timeStopped)) {
            lockData()
        }

        exchanges.startFeed(tickers.distinct(), this)
        exchanges.startBalanceFeed(cryptoAssetRepository.getApiKeysNonRealm(), this)
    }

    fun shouldSecureData(timeSincePaused: Long): Boolean =
            (System.currentTimeMillis() - MINUTE_IN_MILLIS > timeSincePaused)
                    && cryptoAssetRepository.isPasswordSet()

    override fun updateUi(ticker: CryptoPairs) {
        view.updateUi(getOrderedTickerIndex(ticker))
    }

    override fun updateUi(apiBalances: ApiBalances) {
        addNewNonZeroPairsToTickers(apiBalances)
    }

    private fun addNewNonZeroPairsToTickers(apiBalances: ApiBalances) {
        val source = apiKeys.find { it.exchange == apiBalances.exchange }

        if (source != null) {
            val oldSize = tickers.size

            val cryptoPairMap = source.getCryptoPairsMap()
            val nonZeroCryptoPairs = apiBalances.getCryptoPairsForNonZeroBalances(cryptoPairMap)

            nonZeroCryptoPairs.forEach {
                if (tickers.indexOf(it) == -1) {
                    tickers.add(it)
                }
            }

            val newSize = tickers.size

            if (newSize != oldSize) {
                exchanges.startFeed(tickers, this)
            }
        }
    }

    override fun stopFeed() {
        timeStopped = System.currentTimeMillis()
        exchanges.stopFeed()
    }

    override fun showAddNewAssetDialog() {
        navigation.addNewItem()
    }




    override fun showConfirmDeleteAllDialog() {
        view.showConfirmDeleteAllDialog()
    }

    override fun clearAssets() {
        cryptoAssetRepository.clearAllData()

        if (!cryptoAssetRepository.assetsVisible()) {
            cryptoAssetRepository.savePassword("")
            cryptoAssetRepository.setAssetsVisibility(true)
            view.showAssetQuantites(true)
        }

        tickers.clear()
        view.resetUi()
    }

    override fun getNetWorthDisplayString(): String {
        val networth = getNetWorth()

        var combinedString = ""
        networth.forEach { combinedString = combinedString + it + "\n" }

        return if (combinedString.trim().equals("")) "$0.00" else combinedString.trim()
    }

    private fun getNetWorth(): List<String> {
        val netWorth: MutableMap<CurrencyTypes, BigDecimal> = mutableMapOf()


        for ((ticker, data) in exchanges.getData()) {
            var subTotal = netWorth[ticker.currencyType]

            if (subTotal == null) {
                subTotal = BigDecimal("0.0")
            }

            subTotal = subTotal + stringSafeBigDecimal(data.lastPrice) * tickerQuantity(ticker)

            if (subTotal.compareTo(BigDecimal.ZERO) != 0) {
                netWorth.put(ticker.currencyType, subTotal)
            }
        }

        return netWorth.map { currencyFormatter().format(it.value.setScale(2, BigDecimal.ROUND_HALF_UP)) + " " + it.key.name }
    }

    override fun tickerCount(): Int {
        return testList.size
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

    override fun savePassword(password: String) {
        cryptoAssetRepository.savePassword(password)
        cryptoAssetRepository.setAssetsVisibility(true)
    }

    override fun setAssetLockedState() {
        if (cryptoAssetRepository.isPasswordSet()) {
            view.showAssetQuantites(cryptoAssetRepository.assetsVisible())
        } else {
            view.showAssetQuantites(true)
        }
    }

    override fun setAssetsVisibility(isVisible: Boolean) {
        cryptoAssetRepository.setAssetsVisibility(isVisible)
    }

    override fun lockData() {
        if (!cryptoAssetRepository.isPasswordSet()) {
            view.showAddNewPasswordDialog()
        } else {
            cryptoAssetRepository.setAssetsVisibility(false)
            view.showAssetQuantites(false)
        }
    }

    override fun unlockData() {
        view.showUnlockDialog(true)
    }

    override fun unlockData(password: String) {
        if (cryptoAssetRepository.isPasswordValid(password)) {
            cryptoAssetRepository.setAssetsVisibility(true)
            view.showAssetQuantites(true)
        } else {
            view.showUnlockDialog(false)
        }
    }

    override fun recyclerViewType(position: Int): Int {
        val item = testList[position]

        var returnValue = 0

        if (item is CryptoPairs) {
            returnValue = 0
        } else if (item is BasicAuthentication) {
            returnValue = 1
        }

        return returnValue
    }


    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {

        if (testList[position] is CryptoPairs) {

            val currentTradingInfo = getCurrentTradingData(position)

            val ticker = getOrderedTicker(position)

            row.setTicker(ticker.userTicker())
            row.setExchange(ticker.exchange)

            val holdings = tickerQuantityForIndex(position)
            row.setHoldings(holdings.toString())

            if (currentTradingInfo != null) {
                val lastPrice = stringSafeBigDecimal(currentTradingInfo.lastPrice)
                if (lastPrice < BigDecimal.TEN && lastPrice != BigDecimal.ZERO) {
                    row.setLastPrice(smallCurrencyFormatter().format(lastPrice))
                } else {
                    row.setLastPrice(currencyFormatter().format(lastPrice))
                }
                row.setHoldings(holdings.toString())
                row.setNetValue(currencyFormatter().format(netValue(lastPrice, holdings)))
            } else {
                row.setLastPrice("---")
                row.setNetValue("---")
            }

            row.showQuantities(cryptoAssetRepository.assetsVisible() || !cryptoAssetRepository.isPasswordSet())
        } else if (testList[position] is BasicAuthentication) {
            val data = exchanges.getApiData()


            if (data.isNotEmpty()) {

                val blah = testList[position] as BasicAuthentication

                if (data[blah.exchange] != null) {
                    row.setHoldings(data[blah.exchange]!!.ethBalance.toString())
                }

                //row.setHoldings(data[blah.exchange]?.ethBalance!!.toString())
            }

            val exchangeData = exchanges.getData()
            if (exchangeData[CryptoPairs.BITSTAMP_ETH_USD] != null) {
                row.setLastPrice(exchangeData[CryptoPairs.BITSTAMP_ETH_USD]!!.lastPrice)

            }


        }
    }
}
