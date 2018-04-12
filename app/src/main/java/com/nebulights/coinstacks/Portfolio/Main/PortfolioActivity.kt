package com.nebulights.coinstacks.Portfolio.Main

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Extensions.addFragment
import com.nebulights.coinstacks.Network.BlockExplorers.ExplorerProvider
import com.nebulights.coinstacks.Network.BlockExplorers.Explorers
import com.nebulights.coinstacks.Portfolio.Intro.IntroActivity
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.Portfolio.Additions.AdditionsActivity
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.RecordTypes
import io.realm.Realm


class PortfolioActivity : AppCompatActivity(), PortfolioContract.Navigator {

    private var TAG = "MainActivity"
    private lateinit var presenter: PortfolioPresenter
    private var REQUEST_ADD_ITEM = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean(Constants.FIRST_LOAD_KEY, true).apply()
            val firstLoad = prefs.getBoolean(Constants.FIRST_LOAD_KEY, true)

            if (firstLoad) {
                startActivity(Intent(this, IntroActivity::class.java))
            }
        }

        var portfolioFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as PortfolioFragment?

        if (portfolioFragment == null) {
            portfolioFragment = PortfolioFragment.newInstance()
            createPresenter(portfolioFragment)
            addFragment(portfolioFragment, R.id.content_frame)
        } else {
            createPresenter(portfolioFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_ITEM && resultCode == 1) {
            presenter.refreshData()
        } else if (requestCode == REQUEST_ADD_ITEM && resultCode == 2) {
            val exchange = data?.getStringExtra("exchange")

            exchange?.let {
                presenter.deleteApiData(exchange)
            }

            presenter.refreshData()
        }
    }

    private fun createPresenter(portfolioFragment: PortfolioFragment) {
        val exchanges = Exchanges
        exchanges.loadRepositories(ExchangeProvider)

        val explorers = Explorers
        explorers.loadRepositories(ExplorerProvider)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(),
                PreferenceManager.getDefaultSharedPreferences(applicationContext))


        presenter = PortfolioPresenter(exchanges, explorers,
                portfolioFragment, cryptoAssetRepository, this)

//        if (cryptoAssetRepository.isPasswordSet()) {
//            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                    WindowManager.LayoutParams.FLAG_SECURE)
//        }
    }

    override fun addNewItem(recordTypes: RecordTypes) {
        val intent = Intent(this, AdditionsActivity::class.java)
        intent.putExtra("type", recordTypes.name)
        intent.putExtra("editing", false)
        startActivityForResult(intent, REQUEST_ADD_ITEM)
    }

    override fun editItem(item: RecordTypes, cryptoPair: CryptoPairs?, exchange: String, ticker: String) {
        val intent = Intent(this, AdditionsActivity::class.java)
        intent.putExtra("type", item.name)
        intent.putExtra("exchange", exchange)
        intent.putExtra("ticker", ticker)
        intent.putExtra("editing", true)
        startActivityForResult(intent, REQUEST_ADD_ITEM)
    }

    override fun onBackPressed() {
        presenter.setAssetsVisibility(false)
        presenter.setAssetLockedState()
        super.onBackPressed()
    }
}
