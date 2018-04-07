package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Network.exchanges.Models.ApiBalances
import com.nebulights.coinstacks.Portfolio.Main.model.DisplayBalanceItem
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.DisplayBalanceItemTypes

/**
 * Created by babramovitch on 2018-04-03.
 *
 */
object PortfolioDisplayListHelper {

    fun createDisplayList(tickers: MutableList<CryptoPairs>, balances: MutableMap<String, ApiBalances>, cryptoAssetRepository: CryptoAssetContract)
            : MutableList<DisplayBalanceItem> {

        val displayList = createDisplayListFromTickers(tickers, cryptoAssetRepository)
        displayList.addAll(createDisplayListFromBalances(balances))

        return sortListAndAddHeaders(displayList)

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

                previousExchange = sortedList[index].cryptoPair!!.exchange
                previousType = sortedList[index].displayRecordType!!
                sortedList.add(index, DisplayBalanceItem.newHeader(sortedList[index].cryptoPair!!.exchange + " " + sortedList[index].displayRecordType!!.name))
            }
            index += 1
        }

        return sortedList
    }

    private fun isNewExchange(previousExchange: String, exchange: String?): Boolean = previousExchange != exchange
    private fun isNewRecordType(previousType: DisplayBalanceItemTypes, recordType: DisplayBalanceItemTypes?): Boolean = previousType != recordType
    private fun isFirstIndex(index: Int): Boolean = index == 0

}