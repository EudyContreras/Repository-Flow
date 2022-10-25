package com.eudycontreras.repositoryflow.data.sources.handlers

import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import com.eudycontreras.repositoryflow.utils.Result
import com.eudycontreras.repositoryflow.utils.onFailure
import com.eudycontreras.repositoryflow.utils.invalidationFlow

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