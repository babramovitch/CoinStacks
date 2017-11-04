package com.nebulights.crytpotracker.Portfolio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.Network.RepositoryProvider
import com.nebulights.crytpotracker.R
import com.nebulights.crytpotracker.addFragment
import com.squareup.moshi.Moshi

import io.realm.Realm

class PortfolioActivity : AppCompatActivity() {
    private var TAG = "MainActivity"

    lateinit var portfolioPresenter: PortfolioContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var cryptoListFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as PortfolioFragment?

        if (cryptoListFragment == null) {
            cryptoListFragment = PortfolioFragment.newInstance()
            createPresenter(cryptoListFragment)
            addFragment(cryptoListFragment, R.id.content_frame)
        } else {
            createPresenter(cryptoListFragment)
        }

        if (savedInstanceState != null) {
            portfolioPresenter.restoreTickerData(savedInstanceState)
        }
    }

    fun createPresenter(portfolioFragment: PortfolioFragment) {
        portfolioPresenter = PortfolioPresenter(Realm.getDefaultInstance(),
                RepositoryProvider.provideQuadrigaRepository(),
                portfolioFragment,
                listOf(CryptoTypes.BTC, CryptoTypes.BCH, CryptoTypes.ETH, CryptoTypes.LTC),
                Moshi.Builder().build())
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("PRESENTER_TICKER_DATA", portfolioPresenter.saveTickerDataState())
    }

    override fun onDestroy() {
        portfolioPresenter.onDetach()
        super.onDestroy()
    }
}
