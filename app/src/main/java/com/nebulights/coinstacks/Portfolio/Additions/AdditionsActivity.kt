package com.nebulights.coinstacks.Portfolio.Additions

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.nebulights.coinstacks.Extensions.addFragment
import com.nebulights.coinstacks.Network.BlockExplorers.ExplorerProvider
import com.nebulights.coinstacks.Network.BlockExplorers.Explorers
import com.nebulights.coinstacks.Network.exchanges.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.Exchanges
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetRepository
import com.nebulights.coinstacks.R
import io.realm.Realm

class AdditionsActivity : AppCompatActivity(), AdditionsContract.Navigator {

    private var TAG = "AdditionsActivity"
    private lateinit var presenter: AdditionsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras

        if (extras != null && extras.getBoolean("editing", false)) {
            val actionBar = supportActionBar
            actionBar!!.title = "Update"
        }

        var additionsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as AdditionsFragment?

        if (additionsFragment == null) {
            additionsFragment = AdditionsFragment.newInstance(extras)
            createPresenter(additionsFragment)
            addFragment(additionsFragment, R.id.content_frame)
        } else {
            createPresenter(additionsFragment)
        }

        supportActionBar?.elevation = 0.0f
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                close()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createPresenter(additionsFragment: AdditionsFragment) {
        val exchanges = Exchanges
        exchanges.loadRepositories(ExchangeProvider)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(),
                PreferenceManager.getDefaultSharedPreferences(applicationContext))

        val explorers = Explorers
        explorers.loadRepositories(ExplorerProvider)

        presenter = AdditionsPresenter(additionsFragment, exchanges, explorers, cryptoAssetRepository, this)


//        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE)

    }

    override fun close() {
        setResult(1)
        finish()
    }

    override fun closeWithDeletedExchange(responseId: Int, exchange: String) {
        val intent = Intent()
        intent.putExtra("exchange", exchange)
        setResult(responseId, intent)
        finish()
    }
}
