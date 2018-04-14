package com.nebulights.coinstacks.Network.BlockExplorers.Model

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by babramovitch on 4/11/2018.
 *
 */
open class WatchAddressRealm : RealmObject() {

    var exchange: String = ""
    var address: String = ""
    var type: String = ""
    var nickName: String = ""


    fun setCrytpoType(cryptoPairs: CryptoPairs) {
        this.type = cryptoPairs.toString()
    }

    fun getType(): CryptoPairs {
        return CryptoPairs.valueOf(type)
    }

}