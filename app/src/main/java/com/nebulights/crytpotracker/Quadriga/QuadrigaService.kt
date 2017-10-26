package com.nebulights.crytpotracker.Quadriga

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by babramovitch on 10/23/2017.
 */

interface QuadrigaService {
    @GET("v2/ticker")
    fun getCurrentTradingInfo(@Query("book") orderBook: String): Observable<CurrentTradingInfo>

    companion object Factory {
        fun create(): QuadrigaService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .baseUrl("https://api.quadrigacx.com")
                    .build()

            return retrofit.create(QuadrigaService::class.java)
        }
    }
}
