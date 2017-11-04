package com.nebulights.crytpotracker.Portfolio

import android.os.Bundle
import com.nebulights.crytpotracker.CryptoTypes

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioContract {

    interface View {
        fun updateUi(position: Int)
        fun resetUi()
        fun setPresenter(presenter: Presenter)
        fun showCreateAssetDialog(cryptoType: CryptoTypes?, currentQuantity: String)
    }

    interface ViewRow {
        fun setTicker(ticker: String)
        fun setHoldings(holdings: String)
        fun setLastPrice(lastPrice: String)
        fun setNetValue(netValue: String)
    }

    interface Presenter {
        fun restoreTickerData(savedInstanceState: Bundle)
        fun saveTickerDataState(): String
        fun onDetach()
        fun startFeed()
        fun stopFeed()
        fun getNetWorth(): String
        fun createAsset(cryptoType: CryptoTypes, quantity: String, price: String)
        fun tickerCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: ViewRow)
        fun showCreateAssetDialog(position: Int)
        fun clearAssets()

    }
}
