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
        fun startFeed()
        fun stopFeed()
        fun getNetWorth() : String
        fun getCurrentTradingData() : MutableMap<String, CurrentTradingInfo>
        fun addAsset(asset: TrackedAsset)
        fun onDetach()
        fun onAttach()
        fun getCurrentHoldings() : MutableMap<String, Double>
    }

    interface ViewHost {
        fun blahblah()
    }

}
