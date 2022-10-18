package com.eudycontreras.repositoryflow.data.local

import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import java.util.concurrent.ConcurrentHashMap

/**
 * This is just for the sake of the example
 * Some local source (DB, Redix, LocalCache, Etc)
 */
class MemoryCache<T>: LocalSource.Available<LinkDto, T> {
    // Ideally use weak references for memory caches
    private val cacheForSingle: ConcurrentHashMap<LinkDto, T> = ConcurrentHashMap()
    private val cacheForMany: ConcurrentHashMap<LinkDto, List<T>> = ConcurrentHashMap()

    override suspend fun saveData(key: LinkDto, data: T): T {
        return cacheForSingle.run {
            this[key] = data
            getValue(key)
        }
    }

    override suspend fun getData(key: LinkDto): T? {
        return cacheForSingle[key]
    }

    override suspend fun getCollection(key: LinkDto): List<T>? {
        return cacheForMany[key]
    }

    override suspend fun saveCollection(key: LinkDto, data: List<T>): List<T> {
        return cacheForMany.run {
            this[key] = data
            getValue(key)
        }
    }
}