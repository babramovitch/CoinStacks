package com.nebulights.coinstacks.Portfolio.Additions

import com.nebulights.coinstacks.*
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.TradingInfo
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetContract
import com.nebulights.coinstacks.Portfolio.Main.PortfolioContract
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.currencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.smallCurrencyFormatter
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal

import java.math.BigDecimal

/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsPresenter(
        private var view: AdditionsContract.View,
        val cryptoAssetRepository: CryptoAssetContract) : AdditionsContract.Presenter {

    override fun onDetach() {
    }

    private val TAG = "PortfolioPresenter"

    init {
        view.setPresenter(this)
    }

    override fun showCorrectCoinTypeDetails(id: Int) {

        when(id){
            0 -> view.showCoinAddition()
            1 -> view.showAPIAddition()
            2 -> view.showWatchAddition()
        }

    }

    fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        cryptoAssetRepository.createOrUpdateAsset(cryptoPair, quantity, price)
    }

}
