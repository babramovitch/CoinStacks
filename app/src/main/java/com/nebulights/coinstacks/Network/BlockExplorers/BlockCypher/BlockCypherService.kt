package com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher

import com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher.model.AddressResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 4/11/2018.
 *
 */

interface BlockCypherService {
    @GET("v1/eth/main/addrs/{address}/balance")
    fun getBalancesForAddresses(@Path("address") address: String): Observable<AddressResult>
}
