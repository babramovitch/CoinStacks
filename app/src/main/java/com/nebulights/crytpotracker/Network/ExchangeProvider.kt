package com.nebulights.crytpotracker.Network

import com.nebulights.crytpotracker.Network.Bitfinex.BitFinexRepository
import com.nebulights.crytpotracker.Network.Bitfinex.BitFinexService
import com.nebulights.crytpotracker.Network.exchanges.Gdax.GdaxRepository
import com.nebulights.crytpotracker.Network.exchanges.Gdax.GdaxService
import com.nebulights.crytpotracker.Network.exchanges.Gemini.GeminiRepository
import com.nebulights.crytpotracker.Network.exchanges.Gemini.GeminiService
import com.nebulights.crytpotracker.Network.exchanges.Quadriga.QuadrigaRepository
import com.nebulights.crytpotracker.Network.exchanges.Quadriga.QuadrigaService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by babramovitch on 10/25/2017.
 */

object ExchangeProvider {

    val client: OkHttpClient = setupOkHttpClient()

    fun setupOkHttpClient(): OkHttpClient {
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

    fun getAllRepositories(): List<Exchange> {
        val repositories: MutableList<Exchange> = mutableListOf()
        repositories.add(provideQuadrigaRepository())
        repositories.add(provideBitFinixRepository())
        repositories.add(provideGdaxRepository())
        repositories.add(provideGeminiRepository())
        return repositories
    }
}
