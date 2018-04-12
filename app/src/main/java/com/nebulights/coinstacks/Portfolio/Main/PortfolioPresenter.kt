package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Network.BlockExplorers.Explorers
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.smallCurrencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.coinstacks.Portfolio.Main.model.DisplayBalanceItem
import com.nebulights.coinstacks.Types.*
import java.math.BigDecimal
import kotlin.math.exp

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(private var exchanges: Exchanges,
                         private var explorers: Explorers,
                         private var view: PortfolioContract.View,
                         private val cryptoAssetRepository: CryptoAssetContract,
                         private val navigation: PortfolioContract.Navigator) :
        PortfolioContract.Presenter, NetworkCompletionCallback {

    private val TAG = "PortfolioPresenter"

    private lateinit var tickers: MutableList<CryptoPairs>
    private lateinit var displayList: MutableList<DisplayBalanceItem>
    private val temporaryNonZeroBalanceTickers: MutableList<CryptoPairs> = mutableListOf()

    private var timeStopped = System.currentTimeMillis()
    private val MINUTE_IN_MILLIS = 60000

    init {
        view.setPresenter(this)
        refreshData()
    }

    fun refreshData() {
        tickers = cryptoAssetRepository.getTickers()
        displayList = PortfolioDisplayListHelper.createDisplayList(tickers, exchanges.getApiData(), cryptoAssetRepository)
        tickers.addAll(temporaryNonZeroBalanceTickers)
        view.updateUi()

        val watchAddress = cryptoAssetRepository.getWatchAddresses()

        val array: ArrayList<String> = arrayListOf()
        (watchAddress.forEach { watchAddress -> array.add(watchAddress.address) })

        explorers.startFeed(array, this)

    }

    override fun deleteApiData(exchange: String) {
        exchanges.getApiData().remove(exchange)
        temporaryNonZeroBalanceTickers.removeAll { it.exchange == exchange }
    }

    override fun startFeed() {
        if (shouldSecureData(timeStopped)) {
            lockData()
        }

        exchanges.startFeed(tickers, this)
        exchanges.startBalanceFeed(cryptoAssetRepository.getApiKeysNonRealm(), this)
    }

    private fun shouldSecureData(timeSincePaused: Long): Boolean =
            (System.currentTimeMillis() - MINUTE_IN_MILLIS > timeSincePaused)
                    && cryptoAssetRepository.isPasswordSet()

    override fun updateUi(ticker: CryptoPairs) {
        view.updateUi()
    }

    override fun updateUi(apiBalances: ApiBalances) {
        val newTickers = newTickersInBalances(apiBalances)

        refreshData()

        if (newTickers) {
            exchanges.startFeed(tickers, this)
        }
    }

    private fun newTickersInBalances(apiBalances: ApiBalances): Boolean {
        val nonZeroCryptoPairs = apiBalances.getCryptoPairsForNonZeroBalances(apiBalances.displayBalancesAs)

        nonZeroCryptoPairs.forEach {
            if (tickers.indexOf(it) == -1 && temporaryNonZeroBalanceTickers.indexOf(it) == -1) {
                temporaryNonZeroBalanceTickers.add(it)
            }
        }

        return temporaryNonZeroBalanceTickers.isNotEmpty()
    }

    override fun stopFeed() {
        timeStopped = System.currentTimeMillis()
        exchanges.stopFeed()
    }

    override fun addNew(recordTypes: RecordTypes) {
        navigation.addNewItem(recordTypes)
    }

    override fun rowItemClicked(adapterPosition: Int) {
        val item = displayList[adapterPosition]
        when (item.displayRecordType) {
            DisplayBalanceItemTypes.COINS -> navigation.editItem(RecordTypes.COINS, item.cryptoPair, item.cryptoPair!!.exchange, item.cryptoPair!!.userTicker())
            DisplayBalanceItemTypes.API -> navigation.editItem(RecordTypes.API, item.cryptoPair, item.cryptoPair!!.exchange, "")
            DisplayBalanceItemTypes.WATCH -> navigation.editItem(RecordTypes.WATCH, item.cryptoPair, item.cryptoPair!!.exchange, item.cryptoPair!!.userTicker())
            else -> {
                //do nothing when a header is pressed.
            }
        }
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
                netWorth[ticker.currencyType] = subTotal
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

    override fun displayItemCount(): Int = displayList.size

    fun tickerQuantity(ticker: CryptoPairs): BigDecimal =
            cryptoAssetRepository.totalTickerQuantity(ticker)

    private fun getCurrentTradingData(cryptoPair: CryptoPairs): TradingInfo? {
        return exchanges.getData()[cryptoPair]
    }

    fun netValue(price: BigDecimal, holdings: BigDecimal): BigDecimal {
        val value = price * holdings
        return value.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    fun addTickerData(tradingInfo: TradingInfo, cryptoPair: CryptoPairs) {
        exchanges.updateData(cryptoPair, tradingInfo)
    }

    override fun getTickers(): List<CryptoPairs> = tickers

    override fun getTickersForExchange(exchange: String): List<String> =
            exchanges.getTickersForExchange(exchange)

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

    override fun recyclerViewType(position: Int): Int =
            if (displayList[position].displayRecordType == DisplayBalanceItemTypes.HEADER) 0 else 1

    override fun getHeader(position: Int): String = displayList[position].header

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {

        val item = displayList[position]

        if (item.displayRecordType != DisplayBalanceItemTypes.HEADER) {

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

    override fun onDetach() {
        cryptoAssetRepository.close()
    }
}
