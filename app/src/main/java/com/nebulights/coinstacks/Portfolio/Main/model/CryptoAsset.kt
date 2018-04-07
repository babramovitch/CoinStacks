package com.nebulights.coinstacks.Portfolio.model

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CurrencyTypes
import java.math.BigDecimal
import io.realm.RealmObject

open class CryptoAsset : RealmObject() {

    private var currency: String? = null
    private var type: String = ""

    private var amount: String? = null
    private var purchasePrice: String? = null

    fun setCrytpoType(cryptoPairs: CryptoPairs) {
        this.type = cryptoPairs.toString()
    }

    fun getType(): CryptoPairs {
        return CryptoPairs.valueOf(type)
    }

    fun setCurrency(currenncyType: CurrencyTypes) {
        this.currency = currenncyType.toString()
    }

    fun getCurrency(): CryptoPairs? {
        return if (currency != null) CryptoPairs.valueOf(currency!!) else null
    }

    fun getAmount(): BigDecimal {
        return BigDecimal(amount!!)
    }

    fun setAmount(amount: BigDecimal) {
        this.amount = amount.toString()
    }

    fun getPurchasePrice(): BigDecimal? {
        return BigDecimal(purchasePrice!!)
    }

    fun setPurchasePrice(purchasePrice: BigDecimal) {
        this.purchasePrice = purchasePrice.toString()
    }
}