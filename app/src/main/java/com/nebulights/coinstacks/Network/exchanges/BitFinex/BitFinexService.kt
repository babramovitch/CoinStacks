package com.nebulights.coinstacks.Network.exchanges.BitFinex


import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.Balances
import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.CurrentTradingInfo
import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.Payload
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/**
 * Created by babramovitch on 10/23/2017.
 */

interface BitFinexService {
    @GET("v1/pubticker/{book}")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>

    @POST("v1/balances")
    fun getBalances(@Header("Content-Type") contentType: String = "application/json",
                    @Header("Accept") accept: String = "application/json",
                    @Header("X-BFX-APIKEY") apiKey: String,
                    @Header("X-BFX-PAYLOAD") payload: String,
                    @Header("X-BFX-SIGNATURE") signature: String
                    ): Observable<Array<Balances>>
}

