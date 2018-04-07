package com.nebulights.coinstacks.Network.exchanges.Models

import com.nebulights.coinstacks.Types.CryptoPairs
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by babramovitch on 2018-02-26.
 */
open class BasicAuthenticationRealm : RealmObject() {

    @PrimaryKey
    var exchange: String = ""
    var apiKey: String = ""
    var apiSecret: String = ""
    var password: String = ""
    var userName: String = ""
    var cryptoTypes: RealmList<String> = RealmList<String>()


    companion object {
        fun create(exchange: String, apiKey: String, apiSecret: String, password: String, userName: String): BasicAuthenticationRealm {
            val basicAuthentication = BasicAuthenticationRealm()
            basicAuthentication.exchange = exchange
            basicAuthentication.apiKey = apiKey
            basicAuthentication.apiSecret = apiSecret
            basicAuthentication.password = password
            basicAuthentication.userName = userName
            return basicAuthentication
        }
    }

    fun setCryptoTypes(cryptoPairs: List<CryptoPairs>) {
        cryptoTypes.clear()
        cryptoPairs.forEach {
            cryptoTypes.add(it.name)
        }
    }

    fun addCrytpoTypes(cryptoPairs: CryptoPairs) {
        cryptoTypes.add(cryptoPairs.name)
    }

    fun getCryptoTypes(): List<String> {
        return cryptoTypes.toList()
    }


}
//
//
