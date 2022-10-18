package com.eudycontreras.repositoryflow.data.sources.handlers

import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import com.eudycontreras.repositoryflow.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T, R> resolveFlow(
    link: LinkDto,
    remoteSource: RemoteSource<T>,
    mapper: (T) -> R
): Flow<Resource<R>> = flow {
    emit(Resource.Loading)
    try {
        when (val result = remoteSource.get(link)) {
            is Result.Success -> {
                val data = result.data
                emit(Resource.Success(mapper(data)))
            }
            is Result.Failure -> {
                emit(Resource.Failure(ResourceError.RemoteSourceError(result.error)))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        emit(Resource.Failure(ResourceError.Unknown(ex)))
    }
}

fun <T, R> resolveFlowOfMany(
    link: LinkDto,
    remoteSource: RemoteSource<T>,
    mapper: (List<T>) -> List<R>
): Flow<Resource<List<R>>> = flow {
    emit(Resource.Loading)
    try {
        when (val result = remoteSource.getMany(link)) {
            is Result.Success -> {
                val data = result.data
                emit(Resource.Success(mapper(data)))
            }
            is Result.Failure -> {
                emit(Resource.Failure(ResourceError.RemoteSourceError(result.error)))
            }
        }
    } catch (ex: Exception) { // Some more specific network exception here:
        emit(Resource.Failure(ResourceError.Unknown(ex)))
    }
}