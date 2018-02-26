package com.nebulights.coinstacks.Network.exchanges.CexIo


import com.nebulights.coinstacks.Network.exchanges.CexIo.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.CexIo.model.CexBalances
import com.nebulights.coinstacks.Network.exchanges.CexIo.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by babramovitch on 10/23/2017.
 */

interface CexIoService {
    @GET("api/ticker/{crypto}/{currency}")
    fun getCurrentTradingInfo(@Path("crypto") cryptoPart: String, @Path("currency") currencyPart: String): Observable<CurrentTradingInfo>

    @POST("/api/balance/")
    fun getBalances(@Body body: AuthenticationDetails): Observable<CexBalances>
}

