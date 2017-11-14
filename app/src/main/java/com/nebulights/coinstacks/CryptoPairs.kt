package com.nebulights.coinstacks

import com.nebulights.coinstacks.Network.ExchangeProvider

enum class CryptoPairs(val cryptoType: CryptoTypes, val currencyType: CurrencyTypes, val ticker: String, val exchange: String) {

    // QuadrigaCX
    QUADRIGA_BTC_CAD(CryptoTypes.BTC, CurrencyTypes.CAD, "BTC_CAD", ExchangeProvider.QUADRIGACX_NAME),
    QUADRIGA_BCH_CAD(CryptoTypes.BCH, CurrencyTypes.CAD, "BCH_CAD", ExchangeProvider.QUADRIGACX_NAME),
    QUADRIGA_ETH_CAD(CryptoTypes.ETH, CurrencyTypes.CAD, "ETH_CAD", ExchangeProvider.QUADRIGACX_NAME),
    QUADRIGA_LTC_CAD(CryptoTypes.LTC, CurrencyTypes.CAD, "LTC_CAD", ExchangeProvider.QUADRIGACX_NAME),
    QUADRIGA_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "BTC_USD", ExchangeProvider.QUADRIGACX_NAME),

    // BitFinex
    BITFINEX_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", ExchangeProvider.BITFINEX_NAME),
    BITFINEX_BCH_USD(CryptoTypes.BCH, CurrencyTypes.USD, "bchusd", ExchangeProvider.BITFINEX_NAME),
    BITFINEX_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", ExchangeProvider.BITFINEX_NAME),
    BITFINEX_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "ltcusd", ExchangeProvider.BITFINEX_NAME),
    BITFINEX_XMR_USD(CryptoTypes.XMR, CurrencyTypes.USD, "xmrusd", ExchangeProvider.BITFINEX_NAME),
    BITFINEX_XRP_USD(CryptoTypes.XRP, CurrencyTypes.USD, "xrpusd", ExchangeProvider.BITFINEX_NAME),

    // GDAX
    GDAX_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "BTC-USD", ExchangeProvider.GDAX_NAME),
    GDAX_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ETH-USD", ExchangeProvider.GDAX_NAME),
    GDAX_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "LTC-USD", ExchangeProvider.GDAX_NAME),

    GDAX_BTC_EUR(CryptoTypes.BTC, CurrencyTypes.EUR, "BTC-EUR", ExchangeProvider.GDAX_NAME),
    GDAX_ETH_EUR(CryptoTypes.ETH, CurrencyTypes.EUR, "ETH-EUR", ExchangeProvider.GDAX_NAME),
    GDAX_LTC_EUR(CryptoTypes.LTC, CurrencyTypes.EUR, "LTC-EUR", ExchangeProvider.GDAX_NAME),

    GDAX_BTC_GBP(CryptoTypes.BTC, CurrencyTypes.GBP, "BTC-GBP", ExchangeProvider.GDAX_NAME),

    // Gemini
    GEMINI_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", ExchangeProvider.GEMINI_NAME),
    GEMINI_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", ExchangeProvider.GEMINI_NAME),

    // Bitstamp
    BITSTAMP_BTC_USD(CryptoTypes.BTC, CurrencyTypes.USD, "btcusd", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_ETH_USD(CryptoTypes.ETH, CurrencyTypes.USD, "ethusd", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_LTC_USD(CryptoTypes.LTC, CurrencyTypes.USD, "ltcusd", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_XRP_USD(CryptoTypes.XRP, CurrencyTypes.USD, "xrpusd", ExchangeProvider.BITSTAMP_NAME),

    BITSTAMP_BTC_EUR(CryptoTypes.BTC, CurrencyTypes.EUR, "btceur", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_ETH_EUR(CryptoTypes.ETH, CurrencyTypes.EUR, "etheur", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_LTC_EUR(CryptoTypes.LTC, CurrencyTypes.EUR, "ltceur", ExchangeProvider.BITSTAMP_NAME),
    BITSTAMP_XRP_EUR(CryptoTypes.XRP, CurrencyTypes.EUR, "xrpeur", ExchangeProvider.BITSTAMP_NAME),
}

fun CryptoPairs.userTicker(): String {
    return this.cryptoType.name + " : " + this.currencyType.name
}

