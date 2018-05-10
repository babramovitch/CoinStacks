package com.nebulights.coinstacks

import android.app.Application
import android.preference.PreferenceManager
import io.realm.Realm
import com.crashlytics.android.Crashlytics
import com.nebulights.coinstacks.Portfolio.Main.CryptoAssetRepository
import io.fabric.sdk.android.Fabric
import io.realm.RealmConfiguration
import java.io.FileNotFoundException


/**
 * Created by babramovitch on 10/23/2017.
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(this)

        val config1 = RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(Migration())
                .build()

        Realm.setDefaultConfiguration(config1)

        val realm = Realm.getDefaultInstance()

        val cryptoAssetRepository = CryptoAssetRepository(realm, PreferenceManager.getDefaultSharedPreferences(this))
      //  cryptoAssetRepository.clearAllData()
        cryptoAssetRepository.setAssetsVisibility(false)
    }
}