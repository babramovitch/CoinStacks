package com.nebulights.coinstacks.Portfolio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.addFragment
import io.realm.Realm

class PortfolioActivity : AppCompatActivity() {

    private var TAG = "MainActivity"
    private lateinit var presenter: PortfolioPresenter

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

    private fun createPresenter(portfolioFragment: PortfolioFragment) {
        val exchanges = Exchanges
        exchanges.loadRepositories(ExchangeProvider)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(),
                PreferenceManager.getDefaultSharedPreferences(applicationContext))

        presenter = PortfolioPresenter(exchanges,
                portfolioFragment, cryptoAssetRepository)

        if (cryptoAssetRepository.isPasswordSet()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onBackPressed() {
        presenter.setAssetsVisibility(false)
        presenter.setAssetLockedState()
        super.onBackPressed()
    }
}
