package com.nebulights.coinstacks.Network.exchanges.Gemini

import com.nebulights.coinstacks.Network.exchanges.Gemini.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.OkHttpClient

/**
* Created by babramovitch on 10/23/2017.
*/

interface GeminiService {
    @GET("v1/pubticker/{book}")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>

    companion object Factory {
        fun create(client: OkHttpClient): GeminiService {
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("https://api.gemini.com/")
                    .build()

            return retrofit.create(GeminiService::class.java)
        }
    }
}

