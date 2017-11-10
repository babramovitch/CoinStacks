package com.nebulights.crytpotracker.Network

import com.nebulights.crytpotracker.Network.Bitfinex.BitFinexRepository
import com.nebulights.crytpotracker.Network.Bitfinex.BitFinexService
import com.nebulights.crytpotracker.Network.exchanges.Gdax.GdaxRepository
import com.nebulights.crytpotracker.Network.exchanges.Gdax.GdaxService
import com.nebulights.crytpotracker.Network.exchanges.Gemini.GeminiRepository
import com.nebulights.crytpotracker.Network.exchanges.Gemini.GeminiService
import com.nebulights.crytpotracker.Network.exchanges.Quadriga.QuadrigaRepository
import com.nebulights.crytpotracker.Network.exchanges.Quadriga.QuadrigaService

/**
 * Created by babramovitch on 10/25/2017.
 */

object ExchangeProvider {

    fun provideQuadrigaRepository(): QuadrigaRepository {
        return QuadrigaRepository(QuadrigaService.create())
    }

    fun provideBitFinixRepository(): BitFinexRepository {
        return BitFinexRepository(BitFinexService.create())
    }

    fun provideGdaxRepository(): GdaxRepository {
        return GdaxRepository(GdaxService.create())
    }

    fun provideGeminiRepository(): GeminiRepository {
        return GeminiRepository(GeminiService.create())
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
