package com.nebulights.coinstacks.Portfolio.Main.model

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.DisplayBalanceItemTypes
import java.math.BigDecimal

/**
 * Created by babramovitch on 2018-02-22.
 *
 */


class DisplayBalanceItem {

    var currencyPair: CryptoTypes? = null
    var cryptoPair: CryptoPairs? = null
    var displayRecordType: DisplayBalanceItemTypes? = null
    var quantity: BigDecimal? = null
    var address: String? = null
    var addressNickName: String? = ""
    var header: String = ""

    private constructor() {}

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
            return item
        }
    }
}