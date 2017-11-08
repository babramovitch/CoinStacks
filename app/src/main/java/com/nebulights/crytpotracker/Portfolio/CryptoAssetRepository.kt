package com.nebulights.crytpotracker.Portfolio

import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.CurrencyTypes
import com.nebulights.crytpotracker.Portfolio.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.crytpotracker.Portfolio.model.CryptoAsset
import io.realm.Realm
import java.math.BigDecimal

/**
 * Created by babramovitch on 11/6/2017.
 */

interface CryptoAssetContract {
    fun createOrUpdateAsset(cryptoType: CryptoTypes, quantity: String, price: String)
    fun totalTickerQuantity(ticker: CryptoTypes): BigDecimal
    fun clearAllData()
    fun close()
}

class CryptoAssetRepository(val realm: Realm) : CryptoAssetContract {

    override fun createOrUpdateAsset(cryptoType: CryptoTypes, quantity: String, price: String) {
        val asset = realm.where(CryptoAsset::class.java).equalTo("type", cryptoType.toString()).findFirst()

        if (asset == null) {
            createAsset(cryptoType, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        } else {
            updateAsset(asset, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        }
    }

    override fun totalTickerQuantity(ticker: CryptoTypes): BigDecimal {
        var total: BigDecimal = BigDecimal.valueOf(0.0)

        val assets = realm.where(CryptoAsset::class.java).equalTo("type", ticker.toString()).findAll()
        assets.forEach { asset -> total += asset.getAmount() }

        return total
    }

    override fun clearAllData() {
        realm.executeTransaction {
            realm.deleteAll()
        }
    }

    override fun close() {
        realm.close()
    }

    private fun createAsset(cryptoType: CryptoTypes, quantity: BigDecimal, price: BigDecimal) {
        realm.executeTransaction {
            val newAsset = realm.createObject(CryptoAsset::class.java)
            newAsset.setAmount(quantity)
            newAsset.setPurchasePrice(price)
            newAsset.setCurrency(CurrencyTypes.CAD)
            newAsset.setCrytpoType(cryptoType)
        }
    }

    private fun updateAsset(asset: CryptoAsset, quantity: BigDecimal, price: BigDecimal) {
        realm.executeTransaction {
            asset.setAmount(quantity)
            asset.setPurchasePrice(price)
        }
    }
}