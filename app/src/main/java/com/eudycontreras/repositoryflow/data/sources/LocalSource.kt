package com.eudycontreras.repositoryflow.data.sources

import com.eudycontreras.repositoryflow.utils.LinkDto

/**
 * Some local source (DB, Redix, LocalCache, Etc)
 */
sealed interface LocalSource<in Key, Value> {

    interface Unavailable<in Key, Value>: LocalSource<Key, Value>

    interface KeyValue<in Key, Value>: LocalSource<Key, Value> {
        suspend fun getData(key: Key): Value? // We can wrap this to get info about staleness
        suspend fun getCollection(key: Key): List<Value>?
        suspend fun saveData(key: Key, data: Value): Value
        suspend fun saveCollection(key: Key, data: List<Value>): List<Value>
        fun clear()
    }

    companion object {
        inline fun <reified T> getDefault(): Unavailable<LinkDto, T> {
            return object : Unavailable<LinkDto, T> {}
        }
    }
}

interface LocalSingleValueSource<Value> {
    suspend fun setValue(value: Value): Value
    suspend fun getValue(): Value?
    fun clear()
}