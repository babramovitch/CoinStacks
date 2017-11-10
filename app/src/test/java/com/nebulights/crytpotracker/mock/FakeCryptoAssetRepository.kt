package com.nebulights.crytpotracker.mock

import com.nebulights.crytpotracker.CryptoPairs
import com.nebulights.crytpotracker.Portfolio.CryptoAssetContract
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

class FakeCryptoAssetRepository : CryptoAssetContract {
    var assets: MutableMap<CryptoPairs, BigDecimal> = mutableMapOf()

    override fun removeAsset(cryptoPair: CryptoPairs) {
        assets.remove(cryptoPair)
    }

    override fun getTickers(): MutableList<CryptoPairs> {
        val results = assets.map { it.key }
        return results.toMutableList()
    }

    override fun createOrUpdateAsset(cryptoType: CryptoPairs, quantity: String, price: String) {
        assets.put(cryptoType, PortfolioHelpers.stringSafeBigDecimal(quantity))
    }

    override fun totalTickerQuantity(ticker: CryptoPairs): BigDecimal {
        return (if (assets.get(ticker) == null) BigDecimal("0.0") else assets.get(ticker)!!)
    }

    override fun clearAllData() {
        assets.clear()
    }

    override fun close() {
    }
}