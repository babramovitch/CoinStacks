package com.nebulights.crytpotracker

import com.nebulights.crytpotracker.Quadriga.CurrentTradingInfo

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioContract {

    interface View {
        fun updateUi(ticker: String, result: CurrentTradingInfo)
        fun setPresenter(presenter: PortfolioContract.Presenter)
    }

    interface ViewRow {
        fun setTicker(ticker: String)
        fun setHoldings(holdings: String)
        fun setLastPrice(lastPrice: String)
    }

    interface Presenter {
        fun onDetach()
        fun onAttach()

        fun startFeed()
        fun stopFeed()

        fun getNetWorth(): String
        fun getCurrentTradingData(): MutableMap<String, CurrentTradingInfo>
        fun getCurrentTradingData(position: Int): CurrentTradingInfo?
        fun getCurrentHoldings(): MutableMap<String, Double>
        fun getCurrentHoldings(position: Int): Double
        fun getOrderedTicker(position: Int): String

        fun addAsset(asset: TrackedAsset)
        fun tickerCount(): Int
        fun onBindRepositoryRowViewAtPosition(position: Int, row: PortfolioContract.ViewRow)
    }

    interface ViewHost {
        fun blahblah()
    }

}
