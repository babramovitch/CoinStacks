package com.nebulights.coinstacks.mock

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Portfolio.CryptoAssetContract
import com.nebulights.coinstacks.Portfolio.PortfolioHelpers
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

class FakeCryptoAssetRepository : CryptoAssetContract {

    var assetVisibility = false
    var password = ""

    override fun lastUsedExchange(): String {
        return ""
    }

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

    override fun assetsVisible(): Boolean {
        return assetVisibility
    }

    override fun setAssetsVisibility(isVisible: Boolean) {
        assetVisibility = isVisible
    }

    override fun savePassword(password: String) {
        this.password = password
    }

    override fun isPasswordSet(): Boolean {
        return password != ""
    }

    override fun isPasswordValid(password: String): Boolean {
        return this.password == password
    }
}