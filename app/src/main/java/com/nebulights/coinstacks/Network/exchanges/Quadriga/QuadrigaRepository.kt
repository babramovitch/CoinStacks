package com.nebulights.coinstacks.Network.exchanges.Quadriga

import com.nebulights.coinstacks.Network.ApiKeyValidationCallback
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator
import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class QuadrigaRepository(private val service: QuadrigaService) : BaseExchange() {

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false

    override fun feedType(): String = ExchangeProvider.QUADRIGACX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearTickerDisposables()

        tickers.forEach { ticker ->
            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> {
                    Observable.just(
                            generateAuthenticationDetails(basicAuthentication))
                }
                .flatMap<Any> { balances -> service.getBalances(balances) }, basicAuthentication, presenterCallback, networkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ApiKeyValidationCallback, networkDataUpdate: NetworkDataUpdate) {

    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val nonce = System.currentTimeMillis()
        val clientId = basicAuthentication.userName
        val secret = basicAuthentication.apiSecret
        val key = basicAuthentication.apiKey

        val mergedString = nonce.toString() + clientId + key
        val signature = HashGenerator.generateHmacDigest(mergedString.toByteArray(), secret.toByteArray(),
                HashingAlgorithms.HmacSHA256)

        return AuthenticationDetails(key, nonce, signature)

    }
}
