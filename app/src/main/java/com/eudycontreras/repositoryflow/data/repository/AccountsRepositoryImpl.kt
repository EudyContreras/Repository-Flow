package com.eudycontreras.repositoryflow.data.repository

import com.eudycontreras.repositoryflow.data.mapper.toAccount
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.data.sources.handlers.resolveFlow
import com.eudycontreras.repositoryflow.data.sources.handlers.resolveFlowOfMany
import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.domain.repository.AccountsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import kotlinx.coroutines.flow.Flow

class AccountsRepositoryImpl(
    private val localSource: LocalSource<LinkDto, AccountDto>, // This is optional
    private val remoteSource: RemoteSource<AccountDto> // Collection of endpoints/network calls
): AccountsRepository {

    override fun getAccounts(link: LinkDto): Flow<Resource<List<Account>>> {
        val mapper: (List<AccountDto>) -> List<Account> = { items -> items.map { it.toAccount() } }
        return when (localSource) {
            is LocalSource.KeyValue -> resolveFlowOfMany(link, localSource, remoteSource, mapper)
            is LocalSource.Unavailable -> resolveFlowOfMany(link, remoteSource, mapper)
        }
    }

    override fun getAccount(link: LinkDto): Flow<Resource<Account>> {
        val mapper: (AccountDto) -> Account = { it.toAccount() }
        return when (localSource) {
            is LocalSource.KeyValue -> resolveFlow(link, localSource, remoteSource, mapper)
            is LocalSource.Unavailable -> resolveFlow(link, remoteSource, mapper)
        }
    }

    companion object {

        @Volatile
        private var instance: AccountsRepository? = null

        fun getInstance(): AccountsRepository =
            instance ?: throw IllegalStateException("An instance must be build in the app")

        fun buildInstance(
            localSource: LocalSource<LinkDto, AccountDto>,
            remoteSource: RemoteSource<AccountDto>
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