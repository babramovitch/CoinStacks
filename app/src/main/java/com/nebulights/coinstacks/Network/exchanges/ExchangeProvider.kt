package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.Network.exchanges.BitFinex.BitFinexService
import com.nebulights.coinstacks.Network.exchanges.BitFinex.BitFinexRepository
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.BitstampRepository
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.BitstampService
import com.nebulights.coinstacks.Network.exchanges.CexIo.CexIoRepository
import com.nebulights.coinstacks.Network.exchanges.CexIo.CexIoService
import com.nebulights.coinstacks.Network.exchanges.Gdax.GdaxRepository
import com.nebulights.coinstacks.Network.exchanges.Gdax.GdaxService
import com.nebulights.coinstacks.Network.exchanges.Gemini.GeminiRepository
import com.nebulights.coinstacks.Network.exchanges.Gemini.GeminiService
import com.nebulights.coinstacks.Network.exchanges.Quadriga.QuadrigaRepository
import com.nebulights.coinstacks.Network.exchanges.Quadriga.QuadrigaService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by babramovitch on 10/25/2017.
 */

object ExchangeProvider {

    private val client: OkHttpClient = setupOkHttpClient()

    val BITFINEX_NAME = "BitFinex"
    val BITSTAMP_NAME = "Bitstamp"
    val CEXIO_NAME = "CEX.IO"
    val GDAX_NAME = "GDAX"
    val GEMINI_NAME = "Gemini"
    val QUADRIGACX_NAME = "QuadrigaCX"

    val BITFINEX_URL = "https://api.bitfinex.com/"
    val BITSTAMP_URL = "https://www.bitstamp.net/"
    val CEXIO_URL = "https://cex.io/"
    val GDAX_URL = "https://api.gdax.com/"
    val GEMINI_URL = "https://api.gemini.com"
    val QUADRIGACX_URL = "https://api.quadrigacx.com"

    private fun setupOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY // .BODY for full log output
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private fun <T> create(service: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()

        return retrofit.create(service)
    }

    fun provideQuadrigaRepository(): QuadrigaRepository {
        return QuadrigaRepository(create(QuadrigaService::class.java, QUADRIGACX_URL))
    }

    fun provideBitFinixRepository(): BitFinexRepository {
        return BitFinexRepository(create(BitFinexService::class.java, BITFINEX_URL))
    }

    fun provideGdaxRepository(): GdaxRepository {
        return GdaxRepository(create(GdaxService::class.java, GDAX_URL))
    }

    fun provideGeminiRepository(): GeminiRepository {
        return GeminiRepository(create(GeminiService::class.java, GEMINI_URL))
    }

    fun provideBitstampRepository(): BitstampRepository {
        return BitstampRepository(create(BitstampService::class.java, BITSTAMP_URL))
    }

    fun provideCexIoRepository(): CexIoRepository {
        return CexIoRepository(create(CexIoService::class.java, CEXIO_URL))
    }

    fun getAllRepositories(): List<Exchange> {
        val repositories: MutableList<Exchange> = mutableListOf()
        repositories.add(provideQuadrigaRepository())
        repositories.add(provideBitFinixRepository())
        repositories.add(provideGdaxRepository())
        repositories.add(provideGeminiRepository())
        repositories.add(provideBitstampRepository())
        repositories.add(provideCexIoRepository())
        return repositories
    }
}
