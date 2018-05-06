package com.nebulights.coinstacks.Portfolio.Main.model

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.CurrencyTypes
import com.nebulights.coinstacks.Types.DisplayBalanceItemTypes
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by babramovitch on 2018-02-22.
 *
 */


class DisplayBalanceItem {

    var fiatCurrency: CurrencyTypes? = null
    var currencyPair: CryptoTypes? = null
    var cryptoPair: CryptoPairs? = null
    var exchange: String? = null
    var displayRecordType: DisplayBalanceItemTypes? = null
    var quantity: BigDecimal? = null
    var address: String? = null
    var addressNickName: String? = ""
    var header: String = ""
    var lastRowInGroup = false

    private constructor() {}

    fun isFiatRecord() : Boolean {
        return fiatCurrency != null && displayRecordType == DisplayBalanceItemTypes.API
    }

    companion object {
        fun newHeader(header: String): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.header = header
            item.displayRecordType = DisplayBalanceItemTypes.HEADER
            return item
        }

        fun newSubHeader(header: String): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.header = header
            item.displayRecordType = DisplayBalanceItemTypes.SUB_HEADER
            return item
        }

        fun newItem(currencyPair: CryptoTypes, cryptoPair: CryptoPairs, recordType: DisplayBalanceItemTypes,
                    quantity: BigDecimal): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.currencyPair = currencyPair
            item.cryptoPair = cryptoPair
            item.displayRecordType = recordType
            item.quantity = quantity
            item.exchange = cryptoPair.exchange
            return item
        }

        fun newItem(currency: CurrencyTypes, exchange: String, recordType: DisplayBalanceItemTypes,
                    quantity: BigDecimal): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.fiatCurrency = currency
            item.displayRecordType = recordType
            item.quantity = quantity
            item.exchange = exchange
            return item
        }

        fun newItem(currencyPair: CryptoTypes, cryptoPair: CryptoPairs, recordType: DisplayBalanceItemTypes,
                    quantity: BigDecimal, address: String, addressNickName: String): DisplayBalanceItem {
            val item = DisplayBalanceItem()
            item.currencyPair = currencyPair
            item.cryptoPair = cryptoPair
            item.displayRecordType = recordType
            item.quantity = quantity
            item.address = address
            item.addressNickName = addressNickName
            item.exchange = cryptoPair.exchange
            return item
        }
    }

    fun roundedQuantity(): String {
        return if(quantity != null) {
            val digitsToRound = 8
            val roundedQuantity = quantity!!.setScale(digitsToRound, RoundingMode.HALF_UP)
            roundedQuantity.stripTrailingZeros().toPlainString()
        }else{
            "---"
        }
    }
}