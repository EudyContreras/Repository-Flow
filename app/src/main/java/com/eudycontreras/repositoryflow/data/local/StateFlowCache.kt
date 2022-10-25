package com.eudycontreras.repositoryflow.data.local

import com.eudycontreras.repositoryflow.data.sources.LocalSingleValueSource
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * This is just for the sake of the example
 * Some local source (DB, DataStore, Redix, LocalCache, Etc)
 */
class StateFlowCache<T>: LocalSingleValueSource<T> {

    sealed class Cache<out T> {
        data class Available<out T>(val data: T): Cache<T>()
        object Unavailable: Cache<Nothing>()
    }

    private val cache: MutableStateFlow<Cache<T>> = MutableStateFlow(Cache.Unavailable)

    override suspend fun setValue(value: T): T {
        val entry = Cache.Available(value)
        return cache.run {
             emit(entry)
             (this.value as Cache.Available).data
         }
    }

    override suspend fun getValue(): T? {
        return when(val entry = cache.value) {
            is Cache.Available -> entry.data
            else -> null
        }
    }

    override fun clear() {
        cache.value = Cache.Unavailable
    }
}