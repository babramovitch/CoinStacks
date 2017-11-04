package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import java.math.BigDecimal

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
        fun onDetach()
        fun onAttach()

        fun startFeed()
        fun stopFeed()

        fun getNetWorth(): String
        fun getCurrentTradingData(): MutableMap<CryptoTypes, CurrentTradingInfo>
        fun getCurrentTradingData(position: Int): CurrentTradingInfo?
        fun getCurrentHoldings(): MutableMap<CryptoTypes, BigDecimal>
        fun getCurrentHoldings(position: Int): BigDecimal
        fun getOrderedTicker(position: Int): CryptoTypes?

        fun createAsset(cryptoType: CryptoTypes, quantity: String, price: String)
        fun tickerCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: ViewRow)
        fun showCreateAssetDialog(position: Int)
        fun clearAssets()
    }

}
