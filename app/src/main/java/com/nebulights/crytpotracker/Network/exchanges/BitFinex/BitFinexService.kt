package com.nebulights.crytpotracker.Network.Bitfinex


import com.nebulights.crytpotracker.Network.Bitfinex.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 10/23/2017.
 */

interface BitFinexService {
    @GET("v1/pubticker/{book}")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>

    companion object Factory {
        fun create(): BitFinexService {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("https://api.bitfinex.com/")
                    .build()

            return retrofit.create(BitFinexService::class.java)
        }
    }
}
