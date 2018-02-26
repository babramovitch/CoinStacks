package com.nebulights.coinstacks.Network.exchanges.Gemini

import android.util.Base64

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.Gemini.model.BalanceRequest
import com.nebulights.coinstacks.Network.exchanges.Quadriga.Hasher
import com.squareup.moshi.Moshi
import io.reactivex.Observable



/**
 * Created by babramovitch on 10/25/2017.
 */

class GeminiRepository(val service: GeminiService) : BaseExchange(), Exchange {



    override fun feedType(): String = ExchangeProvider.GEMINI_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

//        tickers.forEach { ticker ->
//            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
//                    ticker, presenterCallback, networkDataUpdate)
//        }

        startAccountFeed()

    }

    override fun startAccountFeed() {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails()) }
                .flatMap<Any> { details ->
                    service.getBalances("no-cache",
                            0,
                            "text/plain",
                            details.key,
                            details.payload,
                            details.signature,
                            details.balanceRequest)
                }, feedType())
    }

    override fun generateAuthenticationDetails(): AuthenticationDetails {

        val timestamp = System.currentTimeMillis()


        val key = ""
        val secret = ""

        val balanceRequest = BalanceRequest("/v1/balances", timestamp)

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<BalanceRequest>(BalanceRequest::class.java)

        val balanceRequestJson = jsonAdapter.toJson(balanceRequest)

        val encodedBalanceRequest = Base64.encode(balanceRequestJson.toByteArray(), Base64.NO_WRAP)

        val signature: String = Hasher.generateGeminiDigestHashWithHmac256(encodedBalanceRequest, secret.toByteArray())

        return AuthenticationDetails(key, signature, balanceRequest, String(encodedBalanceRequest))

    }
}