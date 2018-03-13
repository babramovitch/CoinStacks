package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.*
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.TradingInfo
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.smallCurrencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.coinstacks.Portfolio.Main.model.RecordTypes
import com.nebulights.coinstacks.Portfolio.Main.model.DisplayBalanceItem

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
    private var balances: MutableMap<String, ApiBalances>
    private var displayList: MutableList<DisplayBalanceItem> = mutableListOf()

    private var timeStopped = System.currentTimeMillis()
    private val MINUTE_IN_MILLIS = 60000

    init {
        view.setPresenter(this)
        refreshData()
        tickers = cryptoAssetRepository.getTickers()
        balances = exchanges.getApiData()

        refreshData()
    }

    fun refreshData() {
        tickers = cryptoAssetRepository.getTickers()

        balances = exchanges.getApiData()
        displayList.clear()

        tickers.sortBy { it.exchange }

        var previousExchange = ""

        tickers.forEach {
            val item = DisplayBalanceItem.newItem(it.cryptoType, it, RecordTypes.COINS, cryptoAssetRepository.totalTickerQuantity(it))
            displayList.add(item)
        }

        for ((exchange, apiData) in balances) {
            val nonZeroBalances = apiData.getCryptoPairsForNonZeroBalances(apiData.displayBalancesAs)

            if (nonZeroBalances.isNotEmpty()) {

                nonZeroBalances.forEach { type ->
                    val balance = apiData.getBalance(type.cryptoType.name)
                    val displayItem = DisplayBalanceItem.newItem(type.cryptoType, type, RecordTypes.API, balance)
                    displayList.add(displayItem)
                    view.updateUi(1)


                    if (tickers.indexOf(type) == -1) {
                        tickers.add(type)
                    }
                }
            }
        }

        displayList = displayList.sortedWith(compareBy({ it.cryptoPair?.exchange }, { it.recordType }, { it.currencyPair })).toMutableList()

        var index = 0
        var previousType = RecordTypes.HEADER

        while (index < displayList.size) {

            if (index == 0 || previousType != displayList[index].recordType ||
                    previousExchange != displayList[index].cryptoPair?.exchange) {

                previousExchange = displayList[index].cryptoPair!!.exchange
                previousType = displayList[index].recordType!!
                displayList.add(index, DisplayBalanceItem.newHeader(displayList[index].cryptoPair!!.exchange + " " + displayList[index].recordType!!.name))
            }
            index += 1
        }
    }

    override fun startFeed() {
        if (shouldSecureData(timeStopped)) {
            lockData()
        }

        exchanges.startFeed(tickers, this)
        exchanges.startBalanceFeed(cryptoAssetRepository.getApiKeysNonRealm(), this)
    }

    fun shouldSecureData(timeSincePaused: Long): Boolean =
            (System.currentTimeMillis() - MINUTE_IN_MILLIS > timeSincePaused)
                    && cryptoAssetRepository.isPasswordSet()

    override fun updateUi(ticker: CryptoPairs) {
        view.updateUi(getOrderedTickerIndex(ticker))
    }

    override fun updateUi(apiBalances: ApiBalances) {
        updateTickersIfNewTicker(apiBalances)
    }

    private fun updateTickersIfNewTicker(apiBalances: ApiBalances) {

        val nonZeroCryptoPairs = apiBalances.getCryptoPairsForNonZeroBalances(apiBalances.displayBalancesAs)

        var newTicker = false
        nonZeroCryptoPairs.forEach {
            if (tickers.indexOf(it) == -1) {
                newTicker = true
            }
        }

        refreshData()

        if (newTicker) {
            exchanges.startFeed(tickers, this)
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

        return if (combinedString.trim() == "") "$0.00" else combinedString.trim()
    }


    /**
     * @return A list of Strings totaling the net worth in each currency.
     */
    private fun getNetWorth(): List<String> {
        val netWorth: MutableMap<CurrencyTypes, BigDecimal> = mutableMapOf()

        for ((ticker, data) in exchanges.getData()) {
            var subTotal = netWorth[ticker.currencyType]

            if (subTotal == null) {
                subTotal = BigDecimal("0.0")
            }

            subTotal += stringSafeBigDecimal(data.lastPrice) * tickerQuantity(ticker)

            val exchangeBalanceData = exchanges.getApiData()[ticker.exchange]

            if (exchangeBalanceData != null) {
                subTotal += getApiBalanceForMatchingTicker(ticker, exchangeBalanceData) * stringSafeBigDecimal(data.lastPrice)
            }

            if (subTotal.compareTo(BigDecimal.ZERO) != 0) {
                netWorth.put(ticker.currencyType, subTotal)
            }
        }

        return netWorth.map {
            currencyFormatter().format(it.value.setScale(2, BigDecimal.ROUND_HALF_UP)) + " " + it.key.name
        }
    }


    private fun getApiBalanceForMatchingTicker(ticker: CryptoPairs, apiBalances: ApiBalances): BigDecimal {

        var subTotalApi = BigDecimal.ZERO

        val currencyRequested1 = apiBalances.displayBalancesAs[ticker.cryptoType]
        if (currencyRequested1 == ticker) {
            subTotalApi = apiBalances.getBalance(ticker.cryptoType.name)
        }

        return subTotalApi
    }


    override fun tickerCount(): Int = displayList.size


    fun tickerQuantity(ticker: CryptoPairs): BigDecimal =
            cryptoAssetRepository.totalTickerQuantity(ticker)

    private fun getCurrentTradingData(cryptoPair: CryptoPairs): TradingInfo? {
        return exchanges.getData()[cryptoPair]
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    fun getOrderedTickerIndex(cryptoPair: CryptoPairs): Int = tickers.indexOf(cryptoPair)

    fun addTickerData(tradingInfo: TradingInfo, cryptoPair: CryptoPairs) {
        exchanges.updateData(cryptoPair, tradingInfo)
    }

    override fun getTickers(): List<CryptoPairs> = tickers

    override fun getTickersForExchange(exchange: String): List<String> =
            allTickers.filter { ticker ->
                ticker.exchange.toLowerCase() == exchange.toLowerCase()
            }.map { ticker -> ticker.userTicker() }

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
        val item = displayList[position]
        return if (item.recordType == RecordTypes.HEADER) 0 else 1
    }

    override fun getHeader(position: Int): String = displayList[position].header

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {

        val item = displayList[position]

        if (item.recordType != RecordTypes.HEADER) {

            val currentTradingInfo = getCurrentTradingData(item.cryptoPair!!)

            row.setTicker(item.cryptoPair!!.userTicker())
            row.setExchange(item.cryptoPair!!.exchange)
            row.setHoldings(item.quantity!!.toPlainString())

            if (currentTradingInfo != null) {
                val lastPrice = stringSafeBigDecimal(currentTradingInfo.lastPrice)
                if (lastPrice < BigDecimal.TEN && lastPrice != BigDecimal.ZERO) {
                    row.setLastPrice(smallCurrencyFormatter().format(lastPrice))
                } else {
                    row.setLastPrice(currencyFormatter().format(lastPrice))
                }
                row.setHoldings(item.quantity!!.toPlainString())
                row.setNetValue(currencyFormatter().format(netValue(lastPrice, item.quantity!!)))
            } else {
                row.setLastPrice("---")
                row.setNetValue("---")
            }

            row.showQuantities(cryptoAssetRepository.assetsVisible() || !cryptoAssetRepository.isPasswordSet())
        }
    }

    override fun onBindApiBalances(position: Int, row: PortfolioContract.ViewRow) {

    }

    override fun onDetach() {
        cryptoAssetRepository.close()
    }
}
