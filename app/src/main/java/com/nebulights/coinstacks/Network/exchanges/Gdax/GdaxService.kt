package com.nebulights.coinstacks.Network.exchanges.Gdax

import com.nebulights.coinstacks.Network.exchanges.Gdax.model.CurrentTradingInfo
import com.nebulights.coinstacks.Network.exchanges.Gdax.model.Balances
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Created by babramovitch on 10/23/2017.
 */

interface GdaxService {
    @GET("/products/{book}/ticker")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>


    @GET("/accounts")
    fun getBalances(@Header("CB-ACCESS-KEY") accessKey: String,
                    @Header("CB-ACCESS-SIGN") sign: String,
                    @Header("CB-ACCESS-TIMESTAMP") timestamp: String,
                    @Header("CB-ACCESS-PASSPHRASE") passphrase: String,
                    @Header("User-Agent") userAgent: String = "test",
                    @Header("Content-Type") contentType: String = "application/json",
                    @Header("Accept") accept: String = "application/json"): Observable<Array<Balances>>

}



