package com.nebulights.coinstacks.Network.exchanges.Gemini

import android.util.Base64
import com.nebulights.coinstacks.Network.*

import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.BalanceRequest
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.squareup.moshi.Moshi
import io.reactivex.Observable


/**
 * Created by babramovitch on 10/25/2017.
 */

class GeminiRepository(private val service: GeminiService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = false
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.GEMINI_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearTickerDisposables()

        tickers.forEach { ticker ->
            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails(basicAuthentication)) }
                .flatMap<Any> { details ->
                    service.getBalances("no-cache",
                            0,
                            "text/plain",
                            details.key,
                            details.payload,
                            details.signature,
                            details.balanceRequest)
                }, basicAuthentication,
                presenterCallback,
                networkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate) {

    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val timestamp = System.currentTimeMillis()

        val key = basicAuthentication.apiKey
        val secret = basicAuthentication.apiSecret

        val balanceRequest = BalanceRequest("/v1/balances", timestamp)

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<BalanceRequest>(BalanceRequest::class.java)

        val balanceRequestJson = jsonAdapter.toJson(balanceRequest)

        val encodedBalanceRequest = Base64.encode(balanceRequestJson.toByteArray(), Base64.NO_WRAP)

        val signature: String = HashGenerator.generateHmacDigest(encodedBalanceRequest,
                secret.toByteArray(), HashingAlgorithms.HmacSHA384)

        return AuthenticationDetails(key, signature, balanceRequest, String(encodedBalanceRequest))

    }
}