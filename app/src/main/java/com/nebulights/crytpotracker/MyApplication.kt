package com.nebulights.crytpotracker

import android.app.Application

import io.realm.Realm

/**
 * Created by babramovitch on 10/23/2017.
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}