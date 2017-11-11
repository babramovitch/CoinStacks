package com.nebulights.coinstacks.Network

import com.nebulights.coinstacks.Network.exchanges.BitFinex.BitFinexService
import com.nebulights.coinstacks.Network.exchanges.BitFinex.BitFinexRepository
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.BitstampRepository
import com.nebulights.coinstacks.Network.exchanges.Bitstamp.BitstampService
import com.nebulights.coinstacks.Network.exchanges.Gdax.GdaxRepository
import com.nebulights.coinstacks.Network.exchanges.Gdax.GdaxService
import com.nebulights.coinstacks.Network.exchanges.Gemini.GeminiRepository
import com.nebulights.coinstacks.Network.exchanges.Gemini.GeminiService
import com.nebulights.coinstacks.Network.exchanges.Quadriga.QuadrigaRepository
import com.nebulights.coinstacks.Network.exchanges.Quadriga.QuadrigaService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by babramovitch on 10/25/2017.
 */

object ExchangeProvider {

    private val client: OkHttpClient = setupOkHttpClient()

    private fun setupOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE // .BODY for full log output
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    fun provideQuadrigaRepository(): QuadrigaRepository {
        return QuadrigaRepository(QuadrigaService.create(client))
    }

    fun provideBitFinixRepository(): BitFinexRepository {
        return BitFinexRepository(BitFinexService.create(client))
    }

    fun provideGdaxRepository(): GdaxRepository {
        return GdaxRepository(GdaxService.create(client))
    }

    fun provideGeminiRepository(): GeminiRepository {
        return GeminiRepository(GeminiService.create(client))
    }

    fun provideBitstampRepository(): BitstampRepository {
        return BitstampRepository(BitstampService.create(client))
    }

    fun getAllRepositories(): List<Exchange> {
        val repositories: MutableList<Exchange> = mutableListOf()
        repositories.add(provideQuadrigaRepository())
        repositories.add(provideBitFinixRepository())
        repositories.add(provideGdaxRepository())
        repositories.add(provideGeminiRepository())
        repositories.add(provideBitstampRepository())
        return repositories
    }
}
