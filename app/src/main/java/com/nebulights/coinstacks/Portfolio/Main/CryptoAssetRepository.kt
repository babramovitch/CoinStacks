package com.nebulights.coinstacks.Portfolio.Main

import android.content.SharedPreferences
import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.coinstacks.Portfolio.model.CryptoAsset
import com.nebulights.coinstacks.Extensions.applyMe
import io.realm.Realm
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

interface CryptoAssetContract {
    fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String)
    fun totalTickerQuantity(ticker: CryptoPairs): BigDecimal
    fun clearAllData()
    fun close()
    fun removeAsset(cryptoPair: CryptoPairs)
    fun getTickers(): MutableList<CryptoPairs>
    fun lastUsedExchange(): String
    fun assetsVisible(): Boolean
    fun setAssetsVisibility(isVisible: Boolean)
    fun savePassword(password: String)
    fun isPasswordSet(): Boolean
    fun isPasswordValid(password: String): Boolean
}

class CryptoAssetRepository(val realm: Realm, val sharedPreferences: SharedPreferences) : CryptoAssetContract {

    val PREF_LAST_EXCHANGE_SAVED = "lastUsedExchange"
    val PREF_OWNED_ASSETS_VISIBLE = "ownedAssetsVisible"
    val PREF_ASSET_PASSWORD = "assetPassword"

    override fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        val asset = realm.where(CryptoAsset::class.java).equalTo("type", cryptoPair.toString()).findFirst()

        setLastExchangeUsed(cryptoPair.exchange)

        if (asset == null) {
            createAsset(cryptoPair, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        } else {
            updateAsset(asset, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        }
    }

    override fun totalTickerQuantity(ticker: CryptoPairs): BigDecimal {
        var total: BigDecimal = BigDecimal.valueOf(0.0)

        if (assetsVisible() || !isPasswordSet()) {
            val assets = realm.where(CryptoAsset::class.java).equalTo("type", ticker.toString()).findAll()
            assets.forEach { asset -> total += asset.getAmount() }
        }

        return total
    }

    override fun getTickers(): MutableList<CryptoPairs> {
        val results = realm.where(CryptoAsset::class.java).findAll().map { it.getType() }
        return results.toMutableList()
    }

    override fun clearAllData() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }

    override fun close() {
        realm.close()
    }

    private fun createAsset(cryptoPair: CryptoPairs, quantity: BigDecimal, price: BigDecimal) {
        realm.executeTransaction {
            val newAsset = realm.createObject(CryptoAsset::class.java)
            newAsset.setAmount(quantity)
            newAsset.setPurchasePrice(price)
            newAsset.setCurrency(cryptoPair.currencyType)
            newAsset.setCrytpoType(cryptoPair)
        }
    }

    private fun updateAsset(asset: CryptoAsset, quantity: BigDecimal, price: BigDecimal) {
        realm.executeTransaction {
            asset.setAmount(quantity)
            asset.setPurchasePrice(price)
        }
    }

    override fun removeAsset(cryptoPair: CryptoPairs) {
        realm.executeTransaction {
            realm.where(CryptoAsset::class.java).equalTo("type", cryptoPair.toString()).findAll().deleteAllFromRealm()
        }
    }

    private fun setLastExchangeUsed(exchange: String) {
        sharedPreferences.applyMe { putString(PREF_LAST_EXCHANGE_SAVED, exchange) }
    }

    override fun lastUsedExchange(): String {
        return sharedPreferences.getString(PREF_LAST_EXCHANGE_SAVED, "")
    }

    override fun assetsVisible(): Boolean {
        return sharedPreferences.getBoolean(PREF_OWNED_ASSETS_VISIBLE, true)
    }

    override fun setAssetsVisibility(isVisible: Boolean) {
        sharedPreferences.applyMe { putBoolean(PREF_OWNED_ASSETS_VISIBLE, isVisible) }
    }

    override fun isPasswordSet(): Boolean {
        return sharedPreferences.getString(PREF_ASSET_PASSWORD,"") != ""
    }

    override fun savePassword(password: String) {
        sharedPreferences.applyMe { putString(PREF_ASSET_PASSWORD, password) }
    }

    override fun isPasswordValid(password: String): Boolean {
        return sharedPreferences.getString(PREF_ASSET_PASSWORD,"") == password
    }
}