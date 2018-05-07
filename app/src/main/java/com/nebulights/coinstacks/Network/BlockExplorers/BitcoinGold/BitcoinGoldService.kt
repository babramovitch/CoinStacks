package com.nebulights.coinstacks.Network.BlockExplorers.BitcoinGold


import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 4/11/2018.
 *
 */

interface BitcoinGoldService {
    @GET("insight-api/addr/{address}/balance")
    fun getBalancesForAddresses(@Path("address") address: String): Observable<String>
}
