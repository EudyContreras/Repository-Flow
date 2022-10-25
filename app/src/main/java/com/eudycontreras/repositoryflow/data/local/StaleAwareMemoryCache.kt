package com.eudycontreras.repositoryflow.data.local

import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * This is just for the sake of the example
 * Some local source (DB, DataStore, Redix, LocalCache, Etc)
 */
class StaleAwareMemoryCache<T>(
    private val maxLifeTime: Long
): LocalSource.KeyValue<LinkDto, T> {

    data class Cache<T>(val data: T) {
        private val timeStamp: Long = Date().time
        fun isStale(maxLifeTime: Long): Boolean {
            return Date().time < (timeStamp + maxLifeTime)
        }
    }

    // Ideally use weak references for memory caches
    private val cacheForSingle: ConcurrentHashMap<LinkDto, Cache<T>> = ConcurrentHashMap()
    private val cacheForMany: ConcurrentHashMap<LinkDto, Cache<List<T>>> = ConcurrentHashMap()

    override suspend fun saveData(key: LinkDto, data: T): T {
        return cacheForSingle.run {
            this[key] = Cache(data)
            getValue(key).data
        }
    }

    override suspend fun getData(key: LinkDto): T? {
        val entry = cacheForSingle[key]
        return if (entry != null && !entry.isStale(maxLifeTime)) {
            entry.data
        } else run {
            cacheForSingle.remove(key)
            null
        }
    }

    override suspend fun getCollection(key: LinkDto): List<T>? {
        val entry = cacheForMany[key]
        return if (entry != null && !entry.isStale(maxLifeTime)) {
            entry.data
        } else run {
            cacheForMany.remove(key)
            null
        }
    }

    override suspend fun saveCollection(key: LinkDto, data: List<T>): List<T> {
        return cacheForMany.run {
            this[key] = Cache(data)
            getValue(key).data
        }
    }

    override fun clear() {
        cacheForSingle.clear()
        cacheForMany.clear()
    }
}