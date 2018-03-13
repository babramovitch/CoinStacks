package com.nebulights.coinstacks.Portfolio.Main.model

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes
import java.math.BigDecimal

/**
 * Created by babramovitch on 2018-02-22.
 */


enum class RecordTypes {
    COINS, API, WATCH, HEADER
}

class DisplayBalanceItem {

    var currencyPair: CryptoTypes? = null
    var cryptoPair: CryptoPairs? = null
    var recordType: RecordTypes? = null
    var quantity: BigDecimal? = null
    var header: String = ""

    private constructor() {}

    companion object {
        fun newHeader(header: String): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.header = header
            item.recordType = RecordTypes.HEADER
            return item
        }

        fun newItem(currencyPair: CryptoTypes, cryptoPair: CryptoPairs, recordType: RecordTypes,
                    quantity: BigDecimal): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.currencyPair = currencyPair
            item.cryptoPair = cryptoPair
            item.recordType = recordType
            item.quantity = quantity
            return item
        }
    }
}