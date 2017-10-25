package com.nebulights.crytpotracker

import android.app.Application
import android.content.Context

import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by babramovitch on 10/23/2017.
 */

class MyApplication : Application() {

   // lateinit var service: QuadrigaService

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

//        val retrofit = Retrofit.Builder()
//                .baseUrl("https://api.quadrigacx.com")
//                .addConverterFactory(MoshiConverterFactory.create())
//                .build()
//
//        service = retrofit.create(QuadrigaService::class.java)

    }

//    fun quadrigaService(): QuadrigaService {
//        return service
//    }

//    companion object {
//
//        operator fun get(context: Context): MyApplication {
//            return context.applicationContext as MyApplication
//        }
//    }

}