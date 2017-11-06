package com.nebulights.crytpotracker.Portfolio.model

import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.CurrencyTypes
import java.math.BigDecimal
import io.realm.RealmObject

open class CryptoAsset : RealmObject() {

    private var currency: String? = null
    private var type: String? = null

    private var amount: String? = null
    private var purchasePrice: String? = null

    fun setCrytpoType(cryptoTypes: CryptoTypes) {
        this.type = cryptoTypes.toString()
    }

    fun getType(): CryptoTypes? {
        return if (type != null) CryptoTypes.valueOf(type!!) else null
    }

    fun setCurrency(currenncyType: CurrencyTypes) {
        this.currency = currenncyType.toString()
    }

    fun getCurrency(): CryptoTypes? {
        return if (currency != null) CryptoTypes.valueOf(currency!!) else null
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