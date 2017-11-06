package com.nebulights.crytpotracker.Portfolio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.Network.RepositoryProvider
import com.nebulights.crytpotracker.R
import com.nebulights.crytpotracker.addFragment
import io.realm.Realm

class PortfolioActivity : AppCompatActivity() {
    private var TAG = "MainActivity"

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
    }

    private fun createPresenter(portfolioFragment: PortfolioFragment): PortfolioPresenter {
        return PortfolioPresenter(RepositoryProvider.provideQuadrigaRepository(),
                portfolioFragment,
                listOf(CryptoTypes.BTC, CryptoTypes.BCH, CryptoTypes.ETH, CryptoTypes.LTC),
                CryptoAssetRepository(Realm.getDefaultInstance()))
    }
}
