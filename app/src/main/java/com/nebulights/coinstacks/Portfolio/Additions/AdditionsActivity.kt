package com.nebulights.coinstacks.Portfolio.Additions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.Exchanges
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Extensions.addFragment
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetRepository
import io.realm.Realm

class AdditionsActivity : AppCompatActivity(), AdditionsContract.Navigator {

    private var TAG = "AdditionsActivity"
    private lateinit var presenter: AdditionsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var additionsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as AdditionsFragment?

        if (additionsFragment == null) {
            additionsFragment = AdditionsFragment.newInstance()
            createPresenter(additionsFragment)
            addFragment(additionsFragment, R.id.content_frame)
        } else {
            createPresenter(additionsFragment)
        }

        supportActionBar?.elevation = 0.0f
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun createPresenter(additionsFragment: AdditionsFragment) {
        val exchanges = Exchanges
        exchanges.loadRepositories(ExchangeProvider)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(),
                PreferenceManager.getDefaultSharedPreferences(applicationContext))

        presenter = AdditionsPresenter(additionsFragment, exchanges, cryptoAssetRepository, this)


//        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE)

    }

    override fun close() {
        setResult(1)
        finish()
    }

}
