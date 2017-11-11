package com.nebulights.coinstacks.Portfolio

import com.nebulights.coinstacks.isNumber
import java.math.BigDecimal
import java.text.DecimalFormat

/**
* Created by babramovitch on 11/6/2017.
*/

class PortfolioHelpers {

    companion object {
        fun stringSafeBigDecimal(value: String): BigDecimal {
            return if (value.isNumber()) BigDecimal(value) else BigDecimal(0.00)
        }

        fun currencyFormatter(): DecimalFormat {
            return DecimalFormat("$###,###,##0.00")
        }
    }
}