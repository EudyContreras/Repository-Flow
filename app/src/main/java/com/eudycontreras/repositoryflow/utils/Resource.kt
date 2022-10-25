package com.eudycontreras.repositoryflow.utils

sealed class Resource <out T>{
    data class Success<T>(val data: T, val isFromCache: Boolean = false): Resource<T>()
    data class Failure<T>(val error: ResourceError, val onInvalidate: () -> Unit) : Resource<T>()
    object Loading: Resource<Nothing>()
}