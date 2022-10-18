package com.eudycontreras.repositoryflow.data.sources.handlers

import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import com.eudycontreras.repositoryflow.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T, R> resolveFlow(
    link: LinkDto,
    localSource: LocalSource.Available<LinkDto, T>,
    remoteSource: RemoteSource<T>,
    mapper: (T) -> R
): Flow<Resource<R>> = flow {
    emit(Resource.Loading)
    val cacheFallback: suspend (error: ResourceError) -> Unit = { error ->
        val cache = localSource.getData(key = link)
        if (cache != null) {
            emit(Resource.Success(mapper(cache), isFromCache = true))
        } else {
            emit(Resource.Failure(error))
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
                emit(Resource.Failure(ResourceError.RemoteSourceError(result.error)))

                // Here we can alternatively fallback to cache if that's our strategy
                cacheFallback(ResourceError.RemoteSourceError(result.error))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        emit(Resource.Failure(ResourceError.Unknown(ex)))
        // Here we can alternatively fallback to cache if that's our strategy
        cacheFallback(ResourceError.Unknown(ex))
    }
}

fun <T, R> resolveFlowOfMany(
    link: LinkDto,
    localSource: LocalSource.Available<LinkDto, T>,
    remoteSource: RemoteSource<T>,
    mapper: (List<T>) -> List<R>
): Flow<Resource<List<R>>> = flow {
    emit(Resource.Loading)
    val cacheFallback: suspend (error: ResourceError) -> Unit = { error ->
        val cache = localSource.getCollection(key = link)
        if (cache != null) {
            emit(Resource.Success(mapper(cache), isFromCache = true))
        } else {
            emit(Resource.Failure(error))
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
                emit(Resource.Failure(ResourceError.RemoteSourceError(result.error)))

                // Here we can alternatively fallback to cache if that's our strategy
                cacheFallback(ResourceError.RemoteSourceError(result.error))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        emit(Resource.Failure(ResourceError.Unknown(ex)))
        // Here we can alternatively fallback to cache if that's our strategy
        cacheFallback(ResourceError.Unknown(ex))
    }
}