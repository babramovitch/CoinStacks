package com.nebulights.crytpotracker.Quadriga

/**
 * Created by babramovitch on 10/25/2017.
 */
object QuadrigaRepositoryProvider {
    fun provideQuadrigaRepository(): QuadrigaRepository {
        return QuadrigaRepository(QuadrigaService.create())
    }
}