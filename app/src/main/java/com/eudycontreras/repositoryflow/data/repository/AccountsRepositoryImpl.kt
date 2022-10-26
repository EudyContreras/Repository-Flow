package com.eudycontreras.repositoryflow.data.repository

import com.eudycontreras.repositoryflow.data.mapper.toAccount
import com.eudycontreras.repositoryflow.data.remote.api.AccountsService
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDTO
import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.data.sources.handlers.resolveFlowOfMany
import com.eudycontreras.repositoryflow.data.sources.handlers.resolvePollingFlow
import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.domain.repository.AccountsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import kotlinx.coroutines.flow.Flow

class AccountsRepositoryImpl(
    private val localSource: LocalSource<LinkDto, AccountDTO>, // This is optional
    private val remoteSource: AccountsService // Collection of endpoints/network calls
): AccountsRepository {

    override fun getAccounts(link: LinkDto): Flow<Resource<List<Account>>> {
        val mapper: (List<AccountDTO>) -> List<Account> = { items -> items.map { it.toAccount() } }
        return when (localSource) {
            is LocalSource.KeyValue -> resolveFlowOfMany(link, localSource, remoteSource, mapper)
            is LocalSource.Unavailable -> resolveFlowOfMany(link, remoteSource, mapper)
        }
    }

    override fun getTotalBalance(): Flow<Resource<Double>> {
        return resolvePollingFlow(interval = 1000) { remoteSource.getTotalBalance() }
    }

    companion object {

        @Volatile
        private var instance: AccountsRepository? = null

        fun getInstance(): AccountsRepository =
            instance ?: throw IllegalStateException("An instance must be build in the app")

        fun buildInstance(
            localSource: LocalSource<LinkDto, AccountDTO>,
            remoteSource: AccountsService
        ) {
            synchronized(this) {
                if (instance == null) {
                    instance = AccountsRepositoryImpl(
                        localSource = localSource,
                        remoteSource = remoteSource
                    )
                }
            }
        }
    }
}