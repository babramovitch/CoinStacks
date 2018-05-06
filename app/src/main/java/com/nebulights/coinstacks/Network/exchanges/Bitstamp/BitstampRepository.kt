package com.nebulights.coinstacks.Network.exchanges.Bitstamp

import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.*
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.nebulights.coinstacks.Network.security.HashingAlgorithms

import io.reactivex.Observable
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class BitstampRepository(private val service: BitstampService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.BITSTAMP_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearTickerDisposables()
        addToPriceFeed(tickers, presenterCallback,exchangeNetworkDataUpdate)
    }

    override fun addToPriceFeed(
        tickers: List<CryptoPairs>,
        presenterCallback: NetworkCompletionCallback,
        exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate
    ) {
        var delay = 0L

        launch {
            tickers.forEach { ticker ->
                startPriceFeed(service.getCurrentTradingInfo(ticker.ticker), delay,
                    ticker, presenterCallback, exchangeNetworkDataUpdate)

                if (totalDisposables() > Constants.rateLimitSizeThreshold) {
                    delay += 5000
                }

                delay( 500)
            }
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails(basicAuthentication)) }
                .flatMap<Any> { details ->
                    service.getBalances(
                            details.key,
                            details.signature,
                            details.nonce)
                }, basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

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
