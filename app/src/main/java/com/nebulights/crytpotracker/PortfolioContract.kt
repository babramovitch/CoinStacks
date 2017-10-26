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

    interface Presenter {
        fun dosomething(tickers: List<String>)
        fun stop()
        fun loadTradingData()
        fun loadPortfolioData()
        fun addAsset(asset: TrackedAsset)
    }

    interface ViewHost {
        fun blahblah()
    }

}
