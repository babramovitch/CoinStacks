package com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer

import com.nebulights.coinstacks.Network.BlockExplorers.RippleExplorer.Model.AddressBalance
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by babramovitch on 4/11/2018.
 *
 */

interface RippleService {
    @GET("v2/accounts/{address}/balances?currency=XRP")
    fun getBalancesForAddresses(@Path("address") address: String): Observable<AddressBalance>
}
