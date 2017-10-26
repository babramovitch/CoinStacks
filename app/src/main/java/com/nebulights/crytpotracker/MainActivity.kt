package com.nebulights.crytpotracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nebulights.crytpotracker.Quadriga.QuadrigaRepositoryProvider

import io.realm.Realm

class MainActivity : AppCompatActivity(), PortfolioContract.ViewHost {

    lateinit var portfolioPresenter: PortfolioPresenter
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val realm = Realm.getDefaultInstance()

        var cryptoListFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as CryptoListFragment?

        if (cryptoListFragment == null) {
            cryptoListFragment = CryptoListFragment.newInstance("asdf", "asdf")
            addFragment(cryptoListFragment, R.id.content_frame)
        }

        portfolioPresenter = PortfolioPresenter(realm, QuadrigaRepositoryProvider.provideQuadrigaRepository(), this, cryptoListFragment)

    }


    override fun onDestroy() {
        portfolioPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("PRESENTER_STATE", portfolioPresenter.saveState())
    }

    override fun blahblah() {
        Log.i(TAG, "I DID SOMETHING FROM FRAGMENT")
    }
}
