package com.eudycontreras.repositoryflow.data.sources.handlers

import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import com.eudycontreras.repositoryflow.utils.Result
import com.eudycontreras.repositoryflow.utils.onFailure
import com.eudycontreras.repositoryflow.utils.invalidationFlow

/**
 * When we have a local and a remote source we can use
 * different strategies to resolve which source to use.
 * Typically there should always be only one source of truth which is the
 * local source. Other strategies can be used too:
 *
 * * Network-First, (Show new data or cache in case of failure)
 * * Cache-First, (Show cache or get new data if there is no cache)
 * * State-While-Revalidate, (Can show old data while getting new data)
 * * Network-Only, (Changes frequently)
 * * Cache-Only, (Never expected to change)
 * * Etc
 *
 * Typically this is handle within the network layer
 */
inline fun <T, reified R> resolveFlow(
    link: LinkDto,
    localSource: LocalSource.KeyValue<LinkDto, T>,
    remoteSource: RemoteSource<T>,
    crossinline mapper: (T) -> R
) = invalidationFlow<R> { onInvalidate ->
    emit(Resource.Loading)
    val cacheFallback: suspend (error: ResourceError) -> Unit = { error ->
        val cache = localSource.getData(key = link)
        if (cache != null) {
            emit(Resource.Success(mapper(cache), isFromCache = true))
        } else {
            onFailure(error, onInvalidate)
        }
    }
    try {
        when (val result = remoteSource.get(link)) {
            is Result.Success -> {
                val data = result.data
                val truth = localSource.saveData(key = link, data = data)
                emit(Resource.Success(mapper(truth)))
            }
            is Result.Failure -> {
                onFailure(ResourceError.RemoteSourceError(result.error), onInvalidate)

                // Here we can alternatively fallback to cache if that's our strategy
                cacheFallback(ResourceError.RemoteSourceError(result.error))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        onFailure(ResourceError.Unknown(ex), onInvalidate)
        // Here we can alternatively fallback to cache if that's our strategy
        cacheFallback(ResourceError.Unknown(ex))
    }
}

inline fun <T, reified R> resolveFlowOfMany(
    link: LinkDto,
    localSource: LocalSource.KeyValue<LinkDto, T>,
    remoteSource: RemoteSource<T>,
    crossinline mapper: (List<T>) -> List<R>
) = invalidationFlow<List<R>> { onInvalidate ->
    emit(Resource.Loading)
    val cacheFallback: suspend (error: ResourceError) -> Unit = { error ->
        val cache = localSource.getCollection(key = link)
        if (cache != null) {
            emit(Resource.Success(mapper(cache), isFromCache = true))
        } else {
            onFailure(error, onInvalidate)
        }
    }
    try {
        when (val result = remoteSource.getMany(link)) {
            is Result.Success -> {
                val data = result.data
                val truth = localSource.saveCollection(key = link, data = data)
                emit(Resource.Success(mapper(truth)))
            }
            is Result.Failure -> {
                onFailure(ResourceError.RemoteSourceError(result.error), onInvalidate)

                // Here we can alternatively fallback to cache if that's our strategy
                cacheFallback(ResourceError.RemoteSourceError(result.error))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        onFailure(ResourceError.Unknown(ex), onInvalidate)
        // Here we can alternatively fallback to cache if that's our strategy
        cacheFallback(ResourceError.Unknown(ex))
    }
}