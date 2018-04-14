package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Types.RecordTypes
import io.reactivex.Observable


/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsContract {

    interface View {
        fun setPresenter(presenter: Presenter)
        fun showCoinAddition()
        fun showAPIAddition()
        fun showWatchAddition()
        fun showAuthenticationRequirements(userName: Boolean, password: Boolean)
        fun setAuthenticationDetails(basicAuthentication: BasicAuthentication)
        fun setupApiSpinners(basicAuthentication: BasicAuthentication, cryptosForExchange: List<CryptoTypes>, cryptoList: List<String>)
        fun setupCryptoPairSpinner(cryptoList: List<String>)
        fun setEditModeCoinsAndApi()
        fun setEditModeWatch(watchAddress: WatchAddress)
        fun setExchange(position: Int)
        fun setCryptoPair(cryptoPairIndex: Int)
        fun setCryptoQuantity(amount: String)
        fun showVerificationDialog()
        fun closeVerificationDialog()
        fun showValidationErrorDialog(message: String?)
        fun enableSaveButton(isEnabled: Boolean)
    }

    interface Presenter {
        fun onDetach()
        fun showCorrectCoinTypeDetails(recordType: RecordTypes)
        fun lastUsedExchange(exchanges: Array<String>): Int
        fun getTickersForExchange(exchange: String): List<String>
        fun createAsset(exchange: String, selectedPosition: Int, quantity: String, price: String)
        fun cryptosForExchange(exchange: String): List<CryptoTypes>
        fun getTickerForExchangeAndPair(exchange: String, pair: String): List<CryptoPairs>
        fun createAPIKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>)
        fun updateViewsForExchangeSpinnerSelection(exchange: String)
        fun getRecordType(): RecordTypes
        fun deleteRecord(exchange: String, userTicker: String, address: String)
        fun setCryptoQuantity(exchange: String, ticker: String)
        fun verificationComplete()
        fun setInitialScreenAndMode(recordType: String, exchange: String, ticker: String, address: String, editing: Boolean, exchangeList: Array<String>, validator: AdditionsFormValidator)
        fun createFormValidator(observer: Observable<Boolean>)
        fun close()
        fun createWatchAddress(sexchange: String, selectedItemPosition: Int, address: String, nickName: String)
    }

    interface Navigator {
        fun closeWithDeletedExchange(responseId: Int, exchange: String)
        fun close()
    }

}

