package com.nebulights.crytpotracker.mock

import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.Portfolio.CryptoAssetContract
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

class FakeCryptoAssetRepository : CryptoAssetContract {

    var assets: MutableMap<CryptoTypes, BigDecimal> = mutableMapOf()

    override fun createOrUpdateAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        assets.put(cryptoType, PortfolioHelpers.stringSafeBigDecimal(quantity))
    }

    override fun totalTickerQuantity(ticker: CryptoTypes): BigDecimal {
        return (if (assets.get(ticker) == null) BigDecimal("0.0") else assets.get(ticker)!!)
    }

    override fun clearAllData() {
        assets.clear()
    }

    override fun close() {
    }
}