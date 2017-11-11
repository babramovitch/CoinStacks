package com.nebulights.coinstacks.Network.exchanges.Quadriga

import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.CurrentTradingInfo
import io.reactivex.Observable
import okhttp3.OkHttpClient
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
        fun create(client: OkHttpClient): QuadrigaService {
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .baseUrl("https://api.quadrigacx.com")
                    .build()

            return retrofit.create(QuadrigaService::class.java)
        }
    }
}
