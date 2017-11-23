package com.nebulights.coinstacks

import android.app.Application
import android.preference.PreferenceManager
import io.realm.Realm
import com.crashlytics.android.Crashlytics
import com.nebulights.coinstacks.Portfolio.CryptoAssetRepository
import io.fabric.sdk.android.Fabric

/**
 * Created by babramovitch on 10/23/2017.
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(this)

        val cryptoAssetRepository = CryptoAssetRepository(Realm.getDefaultInstance(), PreferenceManager.getDefaultSharedPreferences(this))
        cryptoAssetRepository.setAssetsVisibility(false)
    }
}