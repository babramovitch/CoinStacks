package com.nebulights.coinstacks.Network.exchanges.Bitstamp

import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.CurrentTradingInfo
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Created by babramovitch on 10/23/2017.
 */

interface BitstampService {
    @GET("/api/v2/ticker/{book}/")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>
}
