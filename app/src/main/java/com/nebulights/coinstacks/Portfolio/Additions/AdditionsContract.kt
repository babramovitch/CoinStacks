package com.nebulights.coinstacks.Portfolio.Additions



/**
 * Created by babramovitch on 10/23/2017.
 */

class AdditionsContract {

    interface View {
        fun setPresenter(presenter: Presenter)
        fun showCoinAddition()
        fun showAPIAddition()
        fun showWatchAddition()
    }

    interface Presenter {
        fun onDetach()
        fun showCorrectCoinTypeDetails(id: Int)
    }

}

