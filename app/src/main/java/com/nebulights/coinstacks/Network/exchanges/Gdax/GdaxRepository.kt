package com.nebulights.coinstacks.Network.exchanges.Gdax

import android.util.Base64

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.Gdax.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator
import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class GdaxRepository(private val service: GdaxService) : BaseExchange(), Exchange {

    override fun feedType(): String = ExchangeProvider.GDAX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

        tickers.forEach { ticker ->
            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                    ticker, presenterCallback, networkDataUpdate)
        }

    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication) {
        startAccountBalanceFeed(
                Observable
                        .defer<AuthenticationDetails> {
                            Observable.just(
                                    generateAuthenticationDetails(basicAuthentication))
                        }
                        .flatMap<Any> { details ->
                            service.getBalances(
                                    details.key,
                                    details.signature,
                                    details.timestamp,
                                    details.passphrase)
                        }, feedType())
    }


    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val method = "GET"
        val api = "/accounts"
        val secret = basicAuthentication.apiSecret

        val decodedSecret = Base64.decode(secret, Base64.NO_WRAP)

        val message = timestamp + method + api + ""

        val signatureBytes = HashGenerator.generateHmac(message.toByteArray(), decodedSecret,
                HashingAlgorithms.HmacSHA256)

        val signature = Base64.encodeToString(signatureBytes, Base64.NO_WRAP)

        return AuthenticationDetails(basicAuthentication.apiKey, signature, timestamp, basicAuthentication.password)

    }
}
