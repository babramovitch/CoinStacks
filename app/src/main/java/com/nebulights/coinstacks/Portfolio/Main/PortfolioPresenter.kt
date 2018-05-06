package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Extensions.dp
import com.nebulights.coinstacks.Network.BlockExplorers.Explorers
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressBalance
import com.nebulights.coinstacks.Network.exchanges.Exchanges
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Network.exchanges.Models.TradingInfo
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.smallCurrencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.coinstacks.Portfolio.Main.model.DisplayBalanceItem
import com.nebulights.coinstacks.Types.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

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

    private var tickers: MutableList<CryptoPairs> = mutableListOf()
    private var displayList: MutableList<DisplayBalanceItem> = mutableListOf()
    private val temporaryNonZeroBalanceTickers: MutableList<CryptoPairs> = mutableListOf()

    private var timeStopped = System.currentTimeMillis()
    private val MINUTE_IN_MILLIS = 60000
    private var disposable: CompositeDisposable = CompositeDisposable()

    private var initialLoadComplete = false

    init {
        view.setPresenter(this)
    }

    fun refreshData() {
        explorers.removeNoLongerValidWatchAddresses(cryptoAssetRepository.getWatchAddresses())

        tickers = cryptoAssetRepository.getTickersForAssets()
        if (!cryptoAssetRepository.assetsVisible() && cryptoAssetRepository.isPasswordSet()) {
            tickers.addAll(temporaryNonZeroBalanceTickers)

            cryptoAssetRepository.getTickersForWatchAddress().forEach { ticker ->
                if (tickers.indexOf(ticker) == -1) {
                    tickers.add(ticker)
                }
            }
            displayList = PortfolioDisplayListHelper.createLockedDisplayList(tickers, cryptoAssetRepository)
        } else {
            displayList = PortfolioDisplayListHelper.createDisplayList(tickers, exchanges.getApiData(), explorers.getWatchAddressData(), cryptoAssetRepository)

            tickers.addAll(temporaryNonZeroBalanceTickers)

            cryptoAssetRepository.getTickersForWatchAddress().forEach { ticker ->
                if (tickers.indexOf(ticker) == -1) {
                    tickers.add(ticker)
                }
            }
        }

        if (!initialLoadComplete) {
            view.updateUi()
            view.hasStaleData(exchanges.isAnyDataStale())
        }
    }

    override fun resultFromAdditions(requestCode: Int, resultCode: Int, exchange: String?) {
        if (requestCode == Constants.REQUEST_ADD_ITEM && resultCode == 1) {
            refreshData()
        } else if (requestCode == Constants.REQUEST_ADD_ITEM && resultCode == 2) {
            exchange?.let {
                deleteApiData(exchange)
            }
            refreshData()
        }
    }

    override fun deleteApiData(exchange: String) {
        exchanges.getApiData().remove(exchange)
        temporaryNonZeroBalanceTickers.removeAll { it.exchange == exchange }
    }

    override fun startFeed() {

        refreshData()

        if (shouldSecureData(timeStopped)) {
            lockData()
        }

        exchanges.startFeed(tickers, this)
        exchanges.startBalanceFeed(cryptoAssetRepository.getApiKeysNonRealm(), this)
        explorers.startFeed(cryptoAssetRepository.getWatchAddresses(), this)

        Observable.timer(10, TimeUnit.SECONDS)
                .repeatWhen { result -> result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    initialLoadComplete = true
                    view.hasStaleData(exchanges.isAnyDataStale())
                    view.updateUi()
                }).addTo(disposable)
    }

    private fun shouldSecureData(timeSincePaused: Long): Boolean =
            (System.currentTimeMillis() - MINUTE_IN_MILLIS > timeSincePaused)
                    && cryptoAssetRepository.isPasswordSet()

    override fun updateUi(ticker: CryptoPairs) {
        if (!initialLoadComplete) {
            view.updateUi()
            view.hasStaleData(exchanges.isAnyDataStale())
        }
    }

    override fun updateUi(apiBalances: ApiBalances) {
        val newTickers = newTickersInBalances(apiBalances)

        refreshData()

        if (newTickers.isNotEmpty()) {
            exchanges.addToFeed(newTickers, this)
        }
    }

    override fun updateUi(watchAddress: WatchAddressBalance) {
        refreshData()
    }

    private fun newTickersInBalances(apiBalances: ApiBalances): MutableList<CryptoPairs> {
        val nonZeroCryptoPairs = apiBalances.getCryptoPairsForNonZeroBalances(apiBalances.displayBalancesAs)

        val newTickersFound: MutableList<CryptoPairs> = mutableListOf()


        nonZeroCryptoPairs.forEach {
            if (tickers.indexOf(it) == -1 && temporaryNonZeroBalanceTickers.indexOf(it) == -1) {
                temporaryNonZeroBalanceTickers.add(it)
                newTickersFound.add(it)
            }
        }

        return newTickersFound
    }

    override fun stopFeed() {
        timeStopped = System.currentTimeMillis()
        exchanges.stopFeed()
        explorers.stopFeed()
        initialLoadComplete = false //reset feed so fresh data comes in when restarted
    }

    override fun addNew(recordTypes: RecordTypes) {
        navigation.addNewItem(recordTypes)
    }

    override fun rowItemClicked(adapterPosition: Int) {
        if (cryptoAssetRepository.assetsVisible() || !cryptoAssetRepository.isPasswordSet()) {
            val item = displayList[adapterPosition]
            when (item.displayRecordType) {
                DisplayBalanceItemTypes.COINS -> navigation.editItem(RecordTypes.COINS, item.cryptoPair, item.cryptoPair!!.exchange, item.cryptoPair!!.userTicker())
                DisplayBalanceItemTypes.API -> navigation.editItem(RecordTypes.API, item.cryptoPair, item.exchange!!, "")
                DisplayBalanceItemTypes.WATCH -> navigation.editWatchAddressItem(RecordTypes.WATCH, item.cryptoPair, item.cryptoPair!!.exchange, item.cryptoPair!!.userTicker(), item.address!!)
                DisplayBalanceItemTypes.HEADER -> {/*do nothing*/}
                DisplayBalanceItemTypes.SUB_HEADER -> {/*do nothing*/}
            }
        }
    }

    override fun showConfirmDeleteAllDialog() {
        view.showForgotPasswordlDialog()
    }

    override fun forgotPasswordPressed() {
       view.showForgotPasswordlDialog()
    }

    override fun clearAssets() {
        cryptoAssetRepository.clearAllData()

        if (!cryptoAssetRepository.assetsVisible()) {
            cryptoAssetRepository.savePassword("")
            cryptoAssetRepository.setAssetsVisibility(true)
            view.showAssetQuantites(true)
        }

        tickers.clear()
        exchanges.clearAll()
        explorers.clearAll()
        displayList.clear()
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

            val watchAddressData = explorers.getWatchAddressData().filter { it.value.type == ticker }

            if (watchAddressData.isNotEmpty()) {
                subTotal += getWatchAddressBalanceForMatchingTicker(watchAddressData) * stringSafeBigDecimal(data.lastPrice)
            }

            if (subTotal.compareTo(BigDecimal.ZERO) != 0) {
                netWorth[ticker.currencyType] = subTotal
            }
        }

        exchanges.getApiData().forEach { apiData ->
            val fiatBalances = apiData.value.getNonZeroFiatBalances()
            fiatBalances.forEach {
                netWorth[it] = netWorth[it]?.plus(apiData.value.getBalance(it.name)) ?: apiData.value.getBalance(it.name)
            }
        }

        return netWorth.map {
            currencyFormatter().format(it.value.setScale(2, BigDecimal.ROUND_HALF_UP)) + " " + it.key.name
        }
    }

    private fun getWatchAddressBalanceForMatchingTicker(watchAddressData: Map<String, WatchAddressBalance>): BigDecimal {
        var subTotalWatchAddress = BigDecimal.ZERO
        watchAddressData.forEach { subTotalWatchAddress += BigDecimal(it.value.balance) }
        return subTotalWatchAddress
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
            refreshData()
        }
    }

    override fun unlockData() {
        view.showUnlockDialog(true)
    }

    override fun unlockData(password: String) {
        if (cryptoAssetRepository.isPasswordValid(password)) {
            cryptoAssetRepository.setAssetsVisibility(true)
            view.showAssetQuantites(true)
            refreshData()
        } else {
            view.showUnlockDialog(false)
        }
    }

    override fun recyclerViewType(position: Int): Int {
        return when {
            displayList[position].displayRecordType == DisplayBalanceItemTypes.HEADER -> 0
            displayList[position].displayRecordType == DisplayBalanceItemTypes.SUB_HEADER -> 1
            else -> 2
        }
    }


    override fun getHeader(position: Int): String = displayList[position].header

    override fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow) {

        val item = displayList[position]

        if (item.displayRecordType != DisplayBalanceItemTypes.HEADER) { // This shouldn't happen but being extra safe

            if(!item.isFiatRecord()) {
                val currentTradingInfo = getCurrentTradingData(item.cryptoPair!!)

                row.setTicker(item.cryptoPair!!.userTicker())
                row.setExchange(item.cryptoPair!!.exchange)
                row.setHoldings(item.roundedQuantity())
                row.setWatchAddressNickName(item.addressNickName)

                if (currentTradingInfo != null) {
                    val lastPrice = stringSafeBigDecimal(currentTradingInfo.lastPrice)
                    if (lastPrice < BigDecimal.TEN && lastPrice != BigDecimal.ZERO) {
                        row.setLastPrice(smallCurrencyFormatter().format(lastPrice))
                    } else {
                        row.setLastPrice(currencyFormatter().format(lastPrice))
                    }
                    row.setNetValue(
                        currencyFormatter().format(
                            netValue(
                                lastPrice,
                                item.quantity!!
                            )
                        )
                    )
                } else {
                    row.setLastPrice("---")
                    row.setNetValue("---")
                }

                row.setCryptoCurrency()
                row.showQuantities(cryptoAssetRepository.assetsVisible() || !cryptoAssetRepository.isPasswordSet())

            }else if(item.isFiatRecord()){
                row.setLastPrice("---")

                row.setTicker(item.fiatCurrency!!.name)
                row.setHoldings("")
                row.setLastPrice("")
                row.setFiatCurrency()
                row.setNetValue(item.roundedQuantity())
                row.setWatchAddressNickName(item.addressNickName)
                row.showQuantities(false)
            }

            row.showAddressNickName(!item.addressNickName.isNullOrEmpty())
            row.setRowAsStale(exchanges.isRecordStale(item.cryptoPair, item.exchange!!, item.displayRecordType))

            row.showNetvalue(cryptoAssetRepository.assetsVisible() || !cryptoAssetRepository.isPasswordSet())


            if (item.lastRowInGroup) {
                row.adjustRowBottomMargin(8.dp)
            } else {
                row.adjustRowBottomMargin(0)
            }
        }
    }

    override fun onNetworkError(exchange: String) {

    }

    override fun onNetworkError(exchange: String, error: NetworkErrors) {

    }

    override fun onNetworkError(exchange: String, message: String?) {

    }

    override fun backPressed() {
        setAssetsVisibility(false)
        setAssetLockedState()
    }

    override fun onDetach() {
        disposable.dispose()
        cryptoAssetRepository.close()
    }
}
