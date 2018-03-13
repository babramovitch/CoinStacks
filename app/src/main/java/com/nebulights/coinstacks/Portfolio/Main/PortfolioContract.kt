package com.nebulights.coinstacks.Portfolio.Main

import android.support.v7.widget.RecyclerView
import com.nebulights.coinstacks.CryptoPairs

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioContract {

    interface View {
        fun updateUi(position: Int)
        fun resetUi()
        fun setPresenter(presenter: Presenter)
       // fun showCreateAssetDialog(cryptoPair: CryptoPairs?, currentQuantity: String)

        fun removeItem(position: Int)
        fun showAssetQuantites(isVisible: Boolean)

        fun showAddNewPasswordDialog()
        fun showUnlockDialog(firstAttempt: Boolean)
        fun showConfirmDeleteAllDialog()
    }

    interface ViewRow {
        fun setTicker(ticker: String)
        fun setHoldings(holdings: String)
        fun setLastPrice(lastPrice: String)
        fun setNetValue(netValue: String)
        fun setExchange(exchange: String)
        fun showQuantities(visible: Boolean)
    }

    interface Presenter {
        fun onDetach()
        fun startFeed()
        fun stopFeed()
        fun getNetWorthDisplayString(): String
        //fun createAsset(cryptoPair: CryptoPairs, quantity: String, price: String)

        fun tickerCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: ViewRow)
        //fun showCreateAssetDialog(position: Int)
        fun showAddNewAssetDialog()
        fun clearAssets()
        fun getTickers(): List<CryptoPairs>
        fun getTickersForExchange(exchange: String): List<String>
       // fun removeAsset(cryptoPair: CryptoPairs)
        //fun lastUsedExchange(exchanges: Array<String>): Int
        fun showConfirmDeleteAllDialog()

        //Password related items
        fun setAssetsVisibility(isVisible: Boolean)
        fun setAssetLockedState()
        fun savePassword(password: String)
        fun lockData()
        fun unlockData()
        fun unlockData(password: String)
        fun recyclerViewType(position: Int): Int
        fun onBindApiBalances(position: Int, row: ViewRow)
        fun getHeader(position: Int): String


    }

    interface Navigator {
        fun addNewItem()
    }
}

