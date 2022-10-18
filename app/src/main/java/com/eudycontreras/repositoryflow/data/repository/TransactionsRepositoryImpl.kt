package com.eudycontreras.repositoryflow.data.repository

import com.eudycontreras.repositoryflow.data.mapper.toTransaction
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.data.sources.handlers.resolveFlowOfMany
import com.eudycontreras.repositoryflow.domain.model.Transaction
import com.eudycontreras.repositoryflow.domain.repository.TransactionsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * When we have a local and a remote source we can use
 * different patterns to resolve which source to use.
 * The should always be only one source of truth which is the
 * local source
 */
class TransactionsRepositoryImpl private constructor(
    private val localSource: LocalSource<LinkDto, TransactionDto>, // This is optional
    private val remoteSource: RemoteSource<TransactionDto> // Collection of endpoints/network calls
): TransactionsRepository {

    override fun getTransactions(link: LinkDto): Flow<Resource<List<Transaction>>> {
        val mapper: (List<TransactionDto>) -> List<Transaction> = { items ->
            items.map { it.toTransaction() }
        }
       return when (localSource) {
           is LocalSource.Available -> resolveFlowOfMany(link, localSource, remoteSource, mapper)
           is LocalSource.Unavailable -> resolveFlowOfMany(link, remoteSource, mapper)
       }
    }

    companion object {
        @Volatile
        private var intance: TransactionsRepository? = null

        fun getInstance(): TransactionsRepository =
            intance ?: throw IllegalStateException("An instance must be build in the app")

        fun buildInstance(
            localSource: LocalSource<LinkDto, TransactionDto>,
            remoteSource: RemoteSource<TransactionDto>
        ) {
            synchronized(this) {
                if (intance == null) {
                    intance = TransactionsRepositoryImpl(
                        localSource = localSource,
                        remoteSource = remoteSource
                    )
                }
            }
        }
    }
}