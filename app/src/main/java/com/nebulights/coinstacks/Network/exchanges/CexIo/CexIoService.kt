package com.nebulights.coinstacks.Network.exchanges.CexIo

import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 10/23/2017.
 */

interface CexIoService {
    @GET("api/ticker/{crypto}/{currency}")
    fun getCurrentTradingInfo(@Path("crypto") cryptoPart: String, @Path("currency") currencyPart: String): Observable<CurrentTradingInfo>
}
