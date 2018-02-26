package com.nebulights.coinstacks.Network.exchanges.CexIo

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.CexIo.model.AuthenticationDetails

import com.nebulights.coinstacks.Network.exchanges.Quadriga.Hasher
import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class CexIoRepository(private val service: CexIoService) : BaseExchange() {

    override fun feedType(): String = ExchangeProvider.CEXIO_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

//        tickers.forEach { ticker ->
//            startPriceFeed(service.getCurrentTradingInfo(ticker.cryptoType.name, ticker.currencyType.name),
//                    ticker, presenterCallback, networkDataUpdate)
//        }

        startAccountFeed()
    }

    override fun startAccountFeed() {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails()) }
                .flatMap<Any> { details -> service.getBalances(details) }, feedType())
    }

    override fun generateAuthenticationDetails(): AuthenticationDetails {

        val timestamp = System.currentTimeMillis().toString()

//        An HMAC-SHA256 encoded message containing - a nonce, user ID and API key.
//        The HMAC-SHA256 code must be generated using a secret key that was generated with your API key.
//        This code must be converted to its hexadecimal representation (64 uppercase characters).

        val userId = ""
        val key = ""
        val secret = ""

        val toHash = timestamp + userId + key


        val signature: String = Hasher.generateDigestHashWithHmac256(toHash.toByteArray(), secret.toByteArray()).toUpperCase()

        return AuthenticationDetails(key, signature, timestamp)

    }
}
