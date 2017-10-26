package com.nebulights.crytpotracker.Quadriga

import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class QuadrigaRepository(val getTicker: QuadrigaService) {

    fun getTickerInfo(ticker: String): Observable<CurrentTradingInfo> {
        return getTicker.getCurrentTradingInfo(ticker)
    }
}

