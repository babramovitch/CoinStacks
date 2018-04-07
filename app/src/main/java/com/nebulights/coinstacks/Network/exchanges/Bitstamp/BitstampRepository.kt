package com.nebulights.coinstacks.Network.exchanges.Bitstamp

import com.nebulights.coinstacks.Network.*
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.nebulights.coinstacks.Network.security.HashingAlgorithms

import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitstampRepository(private val service: BitstampService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.BITSTAMP_NAME

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
                    service.getBalances(
                            details.key,
                            details.signature,
                            details.nonce)
                }, basicAuthentication,
                presenterCallback,
                networkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate) {

    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val timestamp = System.currentTimeMillis().toString()

        val customerId = basicAuthentication.userName
        val key = basicAuthentication.apiKey
        val secret = basicAuthentication.apiSecret

        val message = timestamp + customerId + key

        val signature: String = HashGenerator.generateHmacDigest(message.toByteArray(),
                secret.toByteArray(), HashingAlgorithms.HmacSHA256).toUpperCase()

        return AuthenticationDetails(key, signature, timestamp)

    }
}
