package com.eudycontreras.repositoryflow.data.sources

/**
 * Some local source (DB, Redix, LocalCache, Etc)
 */
sealed interface LocalSource<in Key, Value> {
    interface Unavailable<in Key, Value>: LocalSource<Key, Value>
    interface Available<in Key, Value>: LocalSource<Key, Value> {
        suspend fun getData(key: Key): Value? // We can wrap this to get info about staleness
        suspend fun getCollection(key: Key): List<Value>?
        suspend fun saveData(key: Key, data: Value): Value
        suspend fun saveCollection(key: Key, data: List<Value>): List<Value>
    }
}