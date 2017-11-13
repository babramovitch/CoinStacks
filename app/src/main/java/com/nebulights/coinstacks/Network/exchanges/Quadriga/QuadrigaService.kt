package com.nebulights.coinstacks.Network.exchanges.Quadriga

import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.CurrentTradingInfo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by babramovitch on 10/23/2017.
 */

interface QuadrigaService {
    @GET("v2/ticker")
    fun getCurrentTradingInfo(@Query("book") query: String): Observable<CurrentTradingInfo>
}

