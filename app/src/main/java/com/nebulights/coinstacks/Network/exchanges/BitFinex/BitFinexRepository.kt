package com.nebulights.coinstacks.Network.exchanges.BitFinex

import android.util.Base64
import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.*
import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.BitFinex.model.Payload
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitFinexRepository(val service: BitFinexService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = false
    override val passwordRequired: Boolean = false
    override val userNameText = ""

    override fun feedType(): String = ExchangeProvider.BITFINEX_NAME

    override fun userNameText(): String {
        return userNameText
    }

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearTickerDisposables()
        addToPriceFeed(tickers, presenterCallback, exchangeNetworkDataUpdate)
    }

    override fun addToPriceFeed(
            tickers: List<CryptoPairs>,
            presenterCallback: NetworkCompletionCallback,
            exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate
    ) {
        if (totalDisposables() > Constants.rateLimitSizeThreshold) {
            repeatDelayFromSize += 10000
        }

        launch {
            tickers.forEach { ticker ->
                startPriceFeed(service.getCurrentTradingInfo(ticker.ticker), repeatDelayFromSize,
                        ticker, presenterCallback, exchangeNetworkDataUpdate)
                delay(delayBetweenLoopCalls)
            }
        }
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        try {
            val details = generateAuthenticationDetails(basicAuthentication)

            validateAPiKeys(service.getBalances(
                    "application/json",
                    "application/json",
                    details.key,
                    details.payload64,
                    details.signature)
                    , basicAuthentication,
                    presenterCallback,
                    exchangeNetworkDataUpdate)

        } catch (exception: IllegalArgumentException) {
            presenterCallback.validationError(basicAuthentication.exchange, "Error using Secret Key.  Verify all details are correct")
            return
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        startAccountBalanceFeed(
                Observable
                        .defer<AuthenticationDetails> {
                            Observable.just(
                                    generateAuthenticationDetails(basicAuthentication))
                        }
                        .flatMap<Any> { details ->
                            service.getBalances(
                                    "application/json",
                                    "application/json",
                                    details.key,
                                    details.payload64,
                                    details.signature)
                        }, basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {
        val api = "/v1/balances"

        val timestamp = System.currentTimeMillis().toString()
        val key = basicAuthentication.apiKey
        val secret = basicAuthentication.apiSecret

        val payloadObject = Payload(api, timestamp)

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Payload>(Payload::class.java)

        val payloadJson = jsonAdapter.toJson(payloadObject)
        val payloadBase64 = Base64.encodeToString(payloadJson.toByteArray(), Base64.NO_WRAP)

        val signature: String = HashGenerator.generateHmacDigest(payloadBase64.toByteArray(),
                secret.toByteArray(), HashingAlgorithms.HmacSHA384).toLowerCase()

        return AuthenticationDetails(key, payloadBase64, signature)
    }
}

