package com.nebulights.coinstacks.Network.exchanges.CexIo

import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Network.exchanges.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.ExchangeNetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.CexIo.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator


import io.reactivex.Observable
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class CexIoRepository(private val service: CexIoService) : BaseExchange() {

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false
    override val userNameText = "User ID"

    override fun feedType(): String = ExchangeProvider.CEXIO_NAME

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
        var delay = 0L
        launch {
            tickers.forEach { ticker ->

                if (totalDisposables() > Constants.rateLimitSizeThreshold) {
                    delay += 2000
                }

                startPriceFeed(service.getCurrentTradingInfo(ticker.cryptoType.name, ticker.currencyType.name), delay,
                    ticker, presenterCallback, exchangeNetworkDataUpdate)

                delay( 500)
            }
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails(basicAuthentication)) }
                .flatMap<Any> { details -> service.getBalances(details) }, basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        super.validateAPiKeys(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails(basicAuthentication)) }
                .flatMap<Any> { details -> service.getBalances(details) }, basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val timestamp = System.currentTimeMillis().toString()

        val userId = basicAuthentication.userName
        val key = basicAuthentication.apiKey
        val secret = basicAuthentication.apiSecret

        val message = timestamp + userId + key


        val signature: String = HashGenerator.generateHmacDigest(message.toByteArray(),
                secret.toByteArray(), HashingAlgorithms.HmacSHA256).toUpperCase()

        return AuthenticationDetails(key, signature, timestamp)

    }
}
