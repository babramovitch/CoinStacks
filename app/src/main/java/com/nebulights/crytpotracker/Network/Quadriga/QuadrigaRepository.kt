package com.nebulights.crytpotracker.Network.Quadriga

import com.nebulights.crytpotracker.Network.Quadriga.model.CurrentTradingInfo
import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class QuadrigaRepository(val getTicker: QuadrigaService) {

    fun getTickerInfo(ticker: String): Observable<CurrentTradingInfo> {
        return getTicker.getCurrentTradingInfo(ticker)
    }
}

