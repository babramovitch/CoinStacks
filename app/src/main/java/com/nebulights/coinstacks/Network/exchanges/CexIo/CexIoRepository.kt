package com.nebulights.coinstacks.Network.exchanges.CexIo

import com.nebulights.coinstacks.Network.ApiKeyValidationCallback
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.CexIo.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator


import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class CexIoRepository(private val service: CexIoService) : BaseExchange() {

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.CEXIO_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearTickerDisposables()

        tickers.forEach { ticker ->
            startPriceFeed(service.getCurrentTradingInfo(ticker.cryptoType.name, ticker.currencyType.name),
                    ticker, presenterCallback, networkDataUpdate)
        }

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails(basicAuthentication)) }
                .flatMap<Any> { details -> service.getBalances(details) }, basicAuthentication,
                presenterCallback,
                networkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate) {

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
