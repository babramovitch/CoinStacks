package com.nebulights.coinstacks.Portfolio.Main

import android.content.SharedPreferences
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Portfolio.Main.PortfolioHelpers.Companion.stringSafeBigDecimal
import com.nebulights.coinstacks.Portfolio.model.CryptoAsset
import com.nebulights.coinstacks.Extensions.applyMe
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressRealm
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthenticationRealm
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
    fun getTickersForAssets(): MutableList<CryptoPairs>
    fun getTickersForWatchAddress(): MutableList<CryptoPairs>
    //fun getTickers(): MutableList<CryptoPairs>

    fun lastUsedExchange(): String
    fun assetsVisible(): Boolean
    fun setAssetsVisibility(isVisible: Boolean)
    fun savePassword(password: String)
    fun isPasswordSet(): Boolean
    fun isPasswordValid(password: String): Boolean
    fun getApiKeys(): MutableList<BasicAuthenticationRealm>
    fun getApiKeysNonRealm(): MutableList<BasicAuthentication>
    fun createOrUpdateApiKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>)
    fun getApiKeysNonRealmForExchange(exchange: String): BasicAuthentication
    fun removeApiKey(exchange: String)
    fun getWatchAddresses(): MutableList<WatchAddress>
    fun createWatchAddress(cryptoPair: CryptoPairs, address: String, nickName: String)
    fun updateWatchAddress(cryptoPair: CryptoPairs, newAddress: String, originalAddress: String, nickName: String)
    fun getWatchAddress(address: String): WatchAddress?
    fun removeWatchAddress(address: String)

}

class CryptoAssetRepository(val realm: Realm, val sharedPreferences: SharedPreferences) : CryptoAssetContract {
    override fun removeWatchAddress(address: String) {
        realm.executeTransaction {
            realm.where(WatchAddressRealm::class.java).equalTo("address", address).findAll().deleteAllFromRealm()
        }
    }

    override fun getWatchAddress(address: String): WatchAddress? {
        val result = realm.where(WatchAddressRealm::class.java).equalTo("address", address).findFirst()

        return if (result != null) {
            WatchAddress(result.exchange, result.address, result.nickName, result.getType())
        } else {
            null
        }
    }

    override fun getWatchAddresses(): MutableList<WatchAddress> {
        val result = realm.where(WatchAddressRealm::class.java).findAll()

        val watchAddressList: MutableList<WatchAddress> = mutableListOf()

        result.forEach {
            watchAddressList.add(WatchAddress(it.exchange, it.address, it.nickName, it.getType()))
        }

        return watchAddressList
    }

//    override fun createOrUpdateWatchAddress(cryptoPair: CryptoPairs, address: String, nickName: String) {
//        val result = realm.where(WatchAddressRealm::class.java).equalTo("type", cryptoPair.toString()).findFirst()
//
//        if (result == null) {
//            createWatchAddress(cryptoPair, address, nickName)
//        } else {
//            updateWatchAddress(result, cryptoPair, address, nickName)
//        }
//
//        setLastExchangeUsed(cryptoPair.exchange)
//    }

    override fun createWatchAddress(cryptoPair: CryptoPairs, address: String, nickName: String) {
        val result = realm.where(WatchAddressRealm::class.java).equalTo("address", address).findFirst()
        if (result == null) {
            realm.executeTransaction {
                val watchAddressRealm = realm.createObject(WatchAddressRealm::class.java)
                watchAddressRealm.exchange = cryptoPair.exchange
                watchAddressRealm.address = address
                watchAddressRealm.nickName = nickName
                watchAddressRealm.setCrytpoType(cryptoPair)
            }
        } else {
            updateWatchAddress(cryptoPair, address, result.address, nickName)
        }
        setLastExchangeUsed(cryptoPair.exchange)
    }

    override fun updateWatchAddress(cryptoPair: CryptoPairs, newAddress: String, originalAddress: String, nickName: String) {
        val watchAddressRealm = realm.where(WatchAddressRealm::class.java).equalTo("address", originalAddress).findFirst()
        if (watchAddressRealm != null) {
            realm.executeTransaction {
                watchAddressRealm.exchange = cryptoPair.exchange
                watchAddressRealm.address = newAddress
                watchAddressRealm.nickName = nickName
                watchAddressRealm.setCrytpoType(cryptoPair)
            }
        }
        setLastExchangeUsed(cryptoPair.exchange)
    }

    val PREF_LAST_EXCHANGE_SAVED = "lastUsedExchange"
    val PREF_OWNED_ASSETS_VISIBLE = "ownedAssetsVisible"
    val PREF_ASSET_PASSWORD = "assetPassword"


    override fun getApiKeys(): MutableList<BasicAuthenticationRealm> {
        val results = realm.where(BasicAuthenticationRealm::class.java).findAll()
        return results.toMutableList()
    }

    override fun getApiKeysNonRealm(): MutableList<BasicAuthentication> {
        val results = realm.where(BasicAuthenticationRealm::class.java).findAll()

        val basicAuthenticationList: MutableList<BasicAuthentication> = mutableListOf()

        results.forEach {
            basicAuthenticationList.add(BasicAuthentication(it.exchange, it.apiKey, it.apiSecret, it.password, it.userName, it.getCryptoTypes()))
        }

        return basicAuthenticationList

    }

    override fun getApiKeysNonRealmForExchange(exchange: String): BasicAuthentication {
        //  val results = realm.where(BasicAuthenticationRealm::class.java).findAll()

        val results = realm.where(BasicAuthenticationRealm::class.java).equalTo("exchange", exchange).findFirst()

        return if (results != null) {
            BasicAuthentication(results.exchange, results.apiKey, results.apiSecret, results.password, results.userName, results.getCryptoTypes())
        } else {
            BasicAuthentication("", "", "", "", "", listOf())
        }
    }


    override fun createOrUpdateAsset(cryptoPair: CryptoPairs, quantity: String, price: String) {
        val asset = realm.where(CryptoAsset::class.java).equalTo("type", cryptoPair.toString()).findFirst()

        setLastExchangeUsed(cryptoPair.exchange)

        if (asset == null) {
            createAsset(cryptoPair, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        } else {
            updateAsset(asset, stringSafeBigDecimal(quantity), stringSafeBigDecimal(price))
        }
    }

    override fun createOrUpdateApiKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>) {
        val basicAuthentication = realm.where(BasicAuthenticationRealm::class.java).equalTo("exchange", exchange).findFirst()

        setLastExchangeUsed(exchange)

        if (basicAuthentication == null) {
            createApiKey(exchange, userName, apiPassword, apiKey, apiSecret, cryptoPairs)
        } else {
            updateApiKey(basicAuthentication, userName, apiPassword, apiKey, apiSecret, cryptoPairs)
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

    override fun getTickersForAssets(): MutableList<CryptoPairs> {
        val results = realm.where(CryptoAsset::class.java).findAll().map { it.getType() }
        return results.toMutableList()
    }

    override fun getTickersForWatchAddress(): MutableList<CryptoPairs> {
        val results = realm.where(WatchAddressRealm::class.java).findAll().map { it.getType() }
        return results.toMutableList()
    }

    fun getTickers(): MutableList<CryptoPairs> {
        val assetTickers = getTickersForAssets()
        val watchAddressTickers = getTickersForWatchAddress()
        assetTickers.addAll(watchAddressTickers)
        return assetTickers.distinct().toMutableList()

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

    override fun removeApiKey(exchange: String) {
        realm.executeTransaction {
            realm.where(BasicAuthenticationRealm::class.java).equalTo("exchange", exchange).findAll().deleteAllFromRealm()
        }
    }

    private fun createApiKey(exchange: String, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>) {
        realm.executeTransaction {
            val basicAuthenticationRealm = realm.createObject(BasicAuthenticationRealm::class.java, exchange)
            basicAuthenticationRealm.userName = userName
            basicAuthenticationRealm.password = apiPassword
            basicAuthenticationRealm.apiKey = apiKey
            basicAuthenticationRealm.apiSecret = apiSecret
            basicAuthenticationRealm.setCryptoTypes(cryptoPairs)
        }
    }

    private fun updateApiKey(basicAuthenticationRealm: BasicAuthenticationRealm, userName: String, apiPassword: String, apiKey: String, apiSecret: String, cryptoPairs: List<CryptoPairs>) {
        realm.executeTransaction {
            basicAuthenticationRealm.userName = userName
            basicAuthenticationRealm.password = apiPassword
            basicAuthenticationRealm.apiKey = apiKey
            basicAuthenticationRealm.apiSecret = apiSecret
            basicAuthenticationRealm.setCryptoTypes(cryptoPairs)
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
        return sharedPreferences.getString(PREF_ASSET_PASSWORD, "") != ""
    }

    override fun savePassword(password: String) {
        sharedPreferences.applyMe { putString(PREF_ASSET_PASSWORD, password) }
    }

    override fun isPasswordValid(password: String): Boolean {
        return sharedPreferences.getString(PREF_ASSET_PASSWORD, "") == password
    }
}