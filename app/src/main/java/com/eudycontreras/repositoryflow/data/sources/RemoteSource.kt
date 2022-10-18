package com.eudycontreras.repositoryflow.data.sources

import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Result

/**
 * Some network layer
 */
interface RemoteSource<Value> {
    suspend fun post(data: Value)
    suspend fun get(link: LinkDto): Result<Value>
    suspend fun getMany(link: LinkDto): Result<List<Value>>
}