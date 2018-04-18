package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Extensions.isNumber
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressBalance
import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Portfolio.Main.model.DisplayBalanceItem
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.DisplayBalanceItemTypes
import java.math.BigDecimal

/**
 * Created by babramovitch on 2018-04-03.
 *
 */
object PortfolioDisplayListHelper {

    var locked = false

    fun createDisplayList(tickers: MutableList<CryptoPairs>, balances: MutableMap<String, ApiBalances>, watchAddressBalances: MutableMap<String, WatchAddressBalance>, cryptoAssetRepository: CryptoAssetContract)
            : MutableList<DisplayBalanceItem> {

        locked = false

        val displayList = createDisplayListFromTickers(tickers, cryptoAssetRepository)
        displayList.addAll(createDisplayListFromBalances(balances))
        displayList.addAll(createDisplayListFromWatchAddress(watchAddressBalances))

        return sortListAndAddHeaders(displayList)

    }

    fun createLockedDisplayList(tickers: MutableList<CryptoPairs>, cryptoAssetRepository: CryptoAssetContract)
            : MutableList<DisplayBalanceItem> {

        locked = true

        val displayList = createDisplayListFromTickers(tickers, cryptoAssetRepository)
        return sortListAndAddHeaders(displayList)

    }

    private fun createDisplayListFromWatchAddress(watchAddressBalances: MutableMap<String, WatchAddressBalance>): Collection<DisplayBalanceItem> {
        val displayList: MutableList<DisplayBalanceItem> = mutableListOf()


        for ((address, watchAddressBalance) in watchAddressBalances) {
            val balance = watchAddressBalance.balance

            var amount = BigDecimal.ZERO

            if (balance.isNumber()) {
                amount = BigDecimal(balance)
            }


            val displayItem = DisplayBalanceItem.newItem(watchAddressBalance.type.cryptoType,
                    watchAddressBalance.type, DisplayBalanceItemTypes.WATCH, amount, watchAddressBalance.address, watchAddressBalance.nickName)
            displayList.add(displayItem)
        }

        return displayList
    }

    private fun createDisplayListFromTickers(tickers: MutableList<CryptoPairs>,
                                             cryptoAssetRepository: CryptoAssetContract): MutableList<DisplayBalanceItem> {

        val displayList: MutableList<DisplayBalanceItem> = mutableListOf()

        tickers.sortBy { ticker -> ticker.exchange }

        tickers.forEach { ticker ->
            val item = DisplayBalanceItem.newItem(ticker.cryptoType, ticker, DisplayBalanceItemTypes.COINS, cryptoAssetRepository.totalTickerQuantity(ticker))
            displayList.add(item)
        }

        return displayList
    }


    private fun createDisplayListFromBalances(balances: MutableMap<String, ApiBalances>)
            : MutableList<DisplayBalanceItem> {

        val displayList: MutableList<DisplayBalanceItem> = mutableListOf()

        for ((exchange, apiData) in balances) {
            val nonZeroBalances = apiData.getCryptoPairsForNonZeroBalances(apiData.displayBalancesAs)

            if (nonZeroBalances.isNotEmpty()) {

                nonZeroBalances.forEach { type ->
                    val balance = apiData.getBalance(type.cryptoType.name)
                    val displayItem = DisplayBalanceItem.newItem(type.cryptoType, type, DisplayBalanceItemTypes.API, balance)
                    displayList.add(displayItem)
                }
            }
        }

        return displayList

    }

    private fun sortListAndAddHeaders(displayList: MutableList<DisplayBalanceItem>): MutableList<DisplayBalanceItem> {
        var previousExchange = ""
        var previousType = DisplayBalanceItemTypes.HEADER
        var index = 0

        val sortedList = displayList.sortedWith(compareBy({ it.cryptoPair?.exchange }, { it.displayRecordType }, { it.currencyPair })).toMutableList()

        while (index < sortedList.size) {

            if (isFirstIndex(index) ||
                    isNewRecordType(previousType, sortedList[index].displayRecordType!!) ||
                    isNewExchange(previousExchange, sortedList[index].cryptoPair?.exchange)) {

                val isNewExchange = isNewExchange(previousExchange, sortedList[index].cryptoPair?.exchange)

                previousExchange = sortedList[index].cryptoPair!!.exchange
                previousType = sortedList[index].displayRecordType!!

                if (!isNewExchange) {
                    sortedList.add(index, DisplayBalanceItem.newSubHeader(nameForHeader(sortedList[index].displayRecordType!!)))
                } else {

                    if (!isFirstIndex(index)) {
                        sortedList[index - 1].lastRowInGroup = true
                    }

                    sortedList.add(index, DisplayBalanceItem.newHeader(sortedList[index].cryptoPair!!.exchange))
                    if (!locked) {
                        index += 1
                        sortedList.add(index, DisplayBalanceItem.newSubHeader(nameForHeader(sortedList[index].displayRecordType!!)))
                    }
                }
            }
            index += 1
        }

        if (index > 0) {
            sortedList[index - 1].lastRowInGroup = true
        }

        return sortedList
    }

    private fun nameForHeader(displayRecordType: DisplayBalanceItemTypes): String {
        return if (!locked) {
            when (displayRecordType) {
                DisplayBalanceItemTypes.COINS -> "Manual Entries"
                DisplayBalanceItemTypes.API -> "Exchange Balances"
                DisplayBalanceItemTypes.WATCH -> "Address Balances"
                DisplayBalanceItemTypes.HEADER -> ""
                DisplayBalanceItemTypes.SUB_HEADER -> ""
            }
        } else {
            ""
        }
    }

    private fun isNewExchange(previousExchange: String, exchange: String?): Boolean = previousExchange != exchange
    private fun isNewRecordType(previousType: DisplayBalanceItemTypes, recordType: DisplayBalanceItemTypes?): Boolean = previousType != recordType
    private fun isFirstIndex(index: Int): Boolean = index == 0

}