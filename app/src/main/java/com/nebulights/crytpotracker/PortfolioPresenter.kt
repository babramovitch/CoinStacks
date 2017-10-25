package com.nebulights.crytpotracker

import android.os.Bundle
import android.os.Parcelable
import android.util.Log

import io.realm.Realm


/**
 * Created by babramovitch on 10/23/2017.
 */

class PortfolioPresenter(internal var realm: Realm,
                         internal var quadrigaService: QuadrigaService,
                         private var viewHost: PortfolioContract.ViewHost?,
                         private val view: PortfolioContract.View) : PortfolioContract.Presenter {

    init {
        view.setPresenter(this)
    }

    fun onDestroy() {
        this.viewHost = null
    }

    fun restoreState(presenterState: Bundle) {
        //Do Restore
    }

    fun saveState(): Parcelable? {
        return null
    }

    override fun dosomething() {
        Log.i(TAG, "HELLO WORLD I'm A PRESENTER")
        viewHost!!.blahblah()
        view.updateUi()
    }

    override fun loadTradingData() {

    }

    override fun loadPortfolioData() {

    }

    override fun addAsset(asset: TrackedAsset) {

    }

    companion object {
        private val TAG = "PortfolioPresenter"
    }


    //    RealmResults<TrackedAsset> results;
    //    List<CurrentTradingInfo> currentTradingInfoList = new ArrayList<>();
    //     volatile boolean isDownloading = false;
    //    private class TickerTask
    //            implements Runnable {
    //
    //        @Override
    //        @SuppressWarnings("NewApi")
    //        public void run() {
    //            try {
    //                isDownloading = true;
    //                Response<CurrentTradingInfo> btcCad = quadrigaService.getCurrentTradingInfo("BTC_CAD").execute();
    //                CurrentTradingInfo currentTradingInfo = btcCad.body();
    //
    //            } catch(IOException e) {
    ////                if(viewHost != null) {
    ////                    viewHost.doOnUiThread(() -> handleNetworkError());
    ////                }
    //            } finally {
    //                isDownloading = false;
    //            }
    //        }
    //    }

}
