package com.nebulights.crytpotracker

import android.app.Application
import io.realm.Realm
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by babramovitch on 10/23/2017.
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(this)
    }
}