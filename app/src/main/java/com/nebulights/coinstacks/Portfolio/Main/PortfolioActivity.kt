package com.nebulights.coinstacks.Portfolio.Main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Extensions.addFragment
import com.nebulights.coinstacks.Portfolio.Additions.AdditionsActivity
import io.realm.Realm

class PortfolioActivity : AppCompatActivity(), PortfolioContract.Navigator {

    private var TAG = "MainActivity"
    private lateinit var presenter: PortfolioPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var portfolioFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as PortfolioFragment?

        if (portfolioFragment == null) {
            portfolioFragment = PortfolioFragment.newInstance()
            createPresenter(portfolioFragment)
            addFragment(portfolioFragment, R.id.content_frame)
        } else {
            createPresenter(portfolioFragment)
        }
    }

    private fun createPresenter(portfolioFragment: PortfolioFragment) {
        val exchanges = Exchanges
        exchanges.loadRepositories(ExchangeProvider)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(),
                PreferenceManager.getDefaultSharedPreferences(applicationContext))


        presenter = PortfolioPresenter(exchanges,
                portfolioFragment, cryptoAssetRepository, this)

        if (cryptoAssetRepository.isPasswordSet()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun addNewItem() {
        val intent = Intent(this, AdditionsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        presenter.setAssetsVisibility(false)
        presenter.setAssetLockedState()
        super.onBackPressed()
    }
}
