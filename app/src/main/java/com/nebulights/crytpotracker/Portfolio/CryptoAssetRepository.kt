package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.CryptoPairs
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.crytpotracker.Portfolio.model.CryptoAsset
import io.realm.Realm
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

interface CryptoAssetContract {
    fun createOrUpdateAsset(cryptoType: CryptoPairs, quantity: String, price: String)
    fun totalTickerQuantity(ticker: CryptoPairs): BigDecimal
    fun clearAllData()
    fun close()
    fun removeAsset(cryptoType: CryptoPairs)
    fun getTickers(): MutableList<CryptoPairs>
}

class CryptoAssetRepository(val realm: Realm) : CryptoAssetContract {

    override fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        val asset = realm.where(CryptoAsset::class.java).equalTo("type", cryptoPair.toString()).findFirst()

        if (asset == null) {
            createAsset(cryptoPair, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        } else {
            updateAsset(asset, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        }
    }

    override fun totalTickerQuantity(ticker: CryptoPairs): BigDecimal {
        var total: BigDecimal = BigDecimal.valueOf(0.0)

        val assets = realm.where(CryptoAsset::class.java).equalTo("type", ticker.toString()).findAll()
        assets.forEach { asset -> total += asset.getAmount() }

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
}