package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CurrencyTypes
import com.nebulights.coinstacks.Types.RecordTypes

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioContract {

    interface View {
        fun updateUi()
        fun hasStaleData(hasStaleData: Boolean)
        fun resetUi()
        fun setPresenter(presenter: Presenter)
        fun showAssetQuantites(isVisible: Boolean)
        fun showAddNewPasswordDialog()
        fun showUnlockDialog(firstAttempt: Boolean)
        fun showForgotPasswordlDialog()
        fun showWarningdialog()
    }

    interface ViewRow {
        fun setTicker(ticker: String)
        fun setHoldings(holdings: String)
        fun setLastPrice(lastPrice: String)
        fun setNetValue(netValue: String)
        fun setExchange(exchange: String)
        fun showQuantities(visible: Boolean)
        fun showNetvalue(visible: Boolean)
        fun showAddressNickName(visible: Boolean)
        fun setWatchAddressNickName(addressNickName: String?)
        fun adjustRowBottomMargin(amount: Int)
        fun setRowAsStale(isStale: Boolean)
        fun setCryptoCurrency()
        fun setFiatCurrency(currencyTypes: CurrencyTypes)
    }

    interface Presenter {
        fun onDetach()
        fun startFeed()
        fun stopFeed()
        fun getNetWorthDisplayString(): String

        fun displayItemCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: ViewRow)

        fun forgotPasswordPressed()
        fun clearAssets()
        fun getTickers(): List<CryptoPairs>
        fun getTickersForExchange(exchange: String): List<String>

        fun showConfirmDeleteAllDialog()

        fun setAssetsVisibility(isVisible: Boolean)
        fun setAssetLockedState()
        fun savePassword(password: String)
        fun lockDataPressed()
        fun unlockDataPressed()
        fun unlockDataPressed(password: String)

        fun recyclerViewType(position: Int): Int
        fun getHeader(position: Int): String
        fun rowItemClicked(adapterPosition: Int)
        fun addNew(recordTypes: RecordTypes)
        fun deleteApiData(exchange: String)
        fun resultFromAdditions(requestCode: Int, resultCode: Int, stringExtra: String?)
        fun backPressed()
        fun warningPressed()
    }

    interface Navigator {
        fun addNewItem(item: RecordTypes)
        fun editItem(item: RecordTypes, cryptoPair: CryptoPairs?, exchange: String, ticker: String)
        //    fun editCoinsItem(item: RecordTypes, cryptoPair: CryptoPairs?, exchange: String, ticker: String)
        //   fun editApiItem(item: RecordTypes, cryptoPair: CryptoPairs?, exchange: String, ticker: String)
        fun editWatchAddressItem(item: RecordTypes, cryptoPair: CryptoPairs?, exchange: String, ticker: String, address: String)
    }
}

