package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.CryptoPairs

/**
* Created by babramovitch on 10/23/2017.
*/

class PortfolioContract {

    interface View {
        fun updateUi(position: Int)
        fun resetUi()
        fun setPresenter(presenter: Presenter)
        fun showCreateAssetDialog(cryptoPair: CryptoPairs?, currentQuantity: String)
        fun showAddNewAssetDialog()
        fun removeItem(position: Int)
    }

    interface ViewRow {
        fun setTicker(ticker: String)
        fun setHoldings(holdings: String)
        fun setLastPrice(lastPrice: String)
        fun setNetValue(netValue: String)
        fun setExchange(exchange: String)
    }

    interface Presenter {
        fun onDetach()
        fun startFeed()
        fun stopFeed()
        fun getNetWorth(): String
        fun createAsset(cryptoPair: CryptoPairs, quantity: String, price: String)
        fun createAsset(exchange: String, userTicker: String, quantity: String, price: String)
        fun tickerCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: ViewRow)
        fun showCreateAssetDialog(position: Int)
        fun showAddNewAssetDialog()
        fun clearAssets()
        fun getTickers(): List<CryptoPairs>
        fun getTickersForExchange(exchange: String): List<String>
        fun removeAsset(cryptoPair: CryptoPairs)
    }
}

