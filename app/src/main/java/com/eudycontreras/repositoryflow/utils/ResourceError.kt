package com.eudycontreras.repositoryflow.utils

sealed class ResourceError {
    object MissingBody: ResourceError()
    data class RemoteSourceError(val message: String? = null) : ResourceError()
    data class Unknown(val error: Throwable): ResourceError()
}