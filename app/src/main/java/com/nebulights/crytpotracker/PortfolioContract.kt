package com.nebulights.crytpotracker

/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioContract {

    interface View {
        fun updateUi()
        fun setPresenter(presenter: PortfolioContract.Presenter)
    }

    interface Presenter {
        fun dosomething()
        fun loadTradingData()
        fun loadPortfolioData()
        fun addAsset(asset: TrackedAsset)
    }

    interface ViewHost {
        fun blahblah()
    }

}
