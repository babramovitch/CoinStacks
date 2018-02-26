package com.nebulights.coinstacks.Network.exchanges.Gemini

import com.nebulights.coinstacks.Network.exchanges.Gemini.model.BalanceRequest
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.CurrentTradingInfo
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.Balances
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by babramovitch on 10/23/2017.
 */

interface GeminiService {
    @GET("v1/pubticker/{book}")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>

    @POST("v1/balances")
    fun getBalances(@Header("Cache-Control") cacheControl: String = "no-cache",
                    @Header("Content-Length") contentLength: Int = 0,
                    @Header("Content-Type") contentType: String = "text/plain",
                    @Header("X-GEMINI-APIKEY") apiKey: String,
                    @Header("X-GEMINI-PAYLOAD") payload: String,
                    @Header("X-GEMINI-SIGNATURE") signature: String,
                    @Body body: BalanceRequest): Observable<Array<Balances>>
}

