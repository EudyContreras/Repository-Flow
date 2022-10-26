package com.eudycontreras.repositoryflow.data.sources.handlers

import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

inline fun <T, reified R> resolveFlow(
    link: LinkDto,
    remoteSource: RemoteSource<T>,
    crossinline mapper: (T) -> R
) = invalidationFlow<R> { onInvalidate ->
    emit(Resource.Loading)
    try {
        when (val result = remoteSource.get(link)) {
            is Result.Success -> {
                val data = result.data
                emit(Resource.Success(mapper(data)))
            }
            is Result.Failure -> {
                onFailure(ResourceError.RemoteSourceError(result.error), onInvalidate)
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        onFailure(ResourceError.Unknown(ex), onInvalidate)
    }
}

inline fun <reified R> resolvePollingFlow(
    interval: Long,
    crossinline networkCall: suspend () -> Result<R>
) = invalidationFlow<R> { onInvalidate ->
    emit(Resource.Loading)
    poll@ while(currentCoroutineContext().isActive) {
        try {
            when (val result = networkCall()) {
                is Result.Success -> {
                    val data = result.data
                    emit(Resource.Success(data))
                }
                is Result.Failure -> {
                    onFailure(ResourceError.RemoteSourceError(result.error), onInvalidate)
                    break@poll
                }
            }
        } catch (ex: Exception) { // Some more specific network exception here:
            onFailure(ResourceError.Unknown(ex), onInvalidate)
            break@poll
        }
        delay(interval)
    }
}

inline fun <T, reified R> resolveFlowOfMany(
    link: LinkDto,
    remoteSource: RemoteSource<T>,
    crossinline mapper: (List<T>) -> List<R>
) = invalidationFlow<List<R>> { onInvalidate ->
    emit(Resource.Loading)
    try {
        when (val result = remoteSource.getMany(link)) {
            is Result.Success -> {
                val data = result.data
                emit(Resource.Success(mapper(data)))
            }
            is Result.Failure -> {
                onFailure(ResourceError.RemoteSourceError(result.error), onInvalidate)
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        onFailure(ResourceError.Unknown(ex), onInvalidate)
    }
}