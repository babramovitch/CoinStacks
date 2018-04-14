package com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorerBCH

import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer.model.AddressResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 4/11/2018.
 *
 */

interface BlockDozerService {
    @GET("insight-api/addr/{address}/balance")
    fun getBalancesForAddresses(@Path("address") address: String): Observable<String>
}
