package com.nebulights.crytpotracker

import java.math.BigDecimal
import io.realm.RealmObject

open class TrackedAsset : RealmObject() {

    private var currency: String? = null
    private var type: String? = null

    private var amount: String? = null
    private var purchasePrice: String? = null

    fun setCrytpoType(`val`: CryptoTypes) {
        this.type = `val`.toString()
    }

    fun getType(): CryptoTypes? {
        return if (type != null) CryptoTypes.valueOf(type!!) else null
    }

    fun setCurrency(`val`: CryptoTypes) {
        this.currency = `val`.toString()
    }

    fun getCurrency(): CryptoTypes? {
        return if (currency != null) CryptoTypes.valueOf(currency!!) else null
    }

    fun getAmount(): String {
        return amount!!
    }

    fun setAmount(amount: BigDecimal) {
        //this.amount = amount;
    }

    fun getPurchasePrice(): BigDecimal? {
        return null //purchasePrice;
    }

    fun setPurchasePrice(purchasePrice: BigDecimal) {
        //this.purchasePrice = purchasePrice;
    }
}