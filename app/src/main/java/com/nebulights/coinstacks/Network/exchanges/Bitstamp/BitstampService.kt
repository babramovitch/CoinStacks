package com.nebulights.coinstacks.Network.exchanges.Bitstamp

import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.Balances
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by babramovitch on 10/23/2017.
 */

interface BitstampService {
    @GET("/api/v2/ticker/{book}/")
    fun getCurrentTradingInfo(@Path("book") orderBook: String): Observable<CurrentTradingInfo>

    @FormUrlEncoded
    @POST("/api/v2/balance/")
    fun getBalances(@Field("key") key: String,
                    @Field("signature") signature: String,
                    @Field("nonce") nonce: String): Observable<Balances>
}
