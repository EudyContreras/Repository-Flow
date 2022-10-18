package com.eudycontreras.repositoryflow.utils

sealed class Result <out T>{
    data class Success<T>(val data: T): Result<T>()
    data class Failure<T>(val error: String) : Result<T>()
}