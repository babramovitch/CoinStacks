package com.nebulights.coinstacks

enum class CryptoPairs(val CryptoType: CryptoTypes, val currencyType: CurrencyTypes, val ticker: String, val exchange: String) {

    // QuadrigaCX
    QUADRIGA_BTC_CAD(CryptoTypes.BTC, CurrencyTypes.CAD, "BTC_CAD", "QuadrigaCX"),
    QUADRIGA_BCH_CAD(CryptoTypes.BCH, CurrencyTypes.CAD, "BCH_CAD", "QuadrigaCX"),
    QUADRIGA_ETH_CAD(CryptoTypes.ETH, CurrencyTypes.CAD, "ETH_CAD", "QuadrigaCX"),
    QUADRIGA_LTC_CAD(CryptoTypes.LTC, CurrencyTypes.CAD, "LTC_CAD", "QuadrigaCX"),

    // BitFinex
    BITFINEX_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", "BitFinex"),
    BITFINEX_BCH_USD(CryptoTypes.BCH, CurrencyTypes.USD, "bchusd", "BitFinex"),
    BITFINEX_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", "BitFinex"),
    BITFINEX_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "ltcusd", "BitFinex"),

    // GDAX
    GDAX_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "BTC-USD", "GDAX"),
    GDAX_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ETH-USD", "GDAX"),
    GDAX_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "LTC-USD", "GDAX"),

    GDAX_BTC_EUR(CryptoTypes.BTC, CurrencyTypes.EUR, "BTC-EUR", "GDAX"),
    GDAX_ETH_EUR(CryptoTypes.ETH, CurrencyTypes.EUR, "ETH-EUR", "GDAX"),
    GDAX_LTC_EUR(CryptoTypes.LTC, CurrencyTypes.EUR, "LTC-EUR", "GDAX"),

    GDAX_BTC_GBP(CryptoTypes.BTC, CurrencyTypes.GBP, "BTC-GBP", "GDAX"),

    // Gemini
    GEMINI_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", "Gemini"),
    GEMINI_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", "Gemini"),

    // Bitstamp
    BITSTAMP_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", "Bitstamp"),
    BITSTAMP_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", "Bitstamp"),
    BITSTAMP_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "ltcusd", "Bitstamp"),

    BITSTAMP_BTC_EUR(CryptoTypes.BTC, CurrencyTypes.EUR, "btceur", "Bitstamp"),
    BITSTAMP_ETH_EUR(CryptoTypes.ETH, CurrencyTypes.EUR, "etheur", "Bitstamp"),
    BITSTAMP_LTC_EUR(CryptoTypes.LTC, CurrencyTypes.EUR, "ltceur", "Bitstamp"),

}

fun CryptoPairs.userTicker(): String {
    return this.CryptoType.name + " : " + this.currencyType.name
}
