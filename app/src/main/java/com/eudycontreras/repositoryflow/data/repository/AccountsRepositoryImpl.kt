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

/**
 * When we have a local and a remote source we can use
 * different strategies to resolve which source to use.
 * Typically there should always be only one source of truth which is the
 * local source. Other strategies can be used too:
 *
 * * Network-First,
 * * Cache-First,
 * * State-While-Revalidate,
 * * Network-Only,
 * * Cache-Only,
 * * Etc
 *
 * Typically this is handle within the network layer
 */
class AccountsRepositoryImpl(
    private val localSource: LocalSource<LinkDto, AccountDto>,  // This is optional
    private val remoteSource: RemoteSource<AccountDto> // Collection of endpoints/network calls
): AccountsRepository {

    override fun getAccounts(link: LinkDto): Flow<Resource<List<Account>>> {
        val mapper: (List<AccountDto>) -> List<Account> = { items -> items.map { it.toAccount() } }
        return when (localSource) {
            is LocalSource.Available -> resolveFlowOfMany(link, localSource, remoteSource, mapper)
            is LocalSource.Unavailable -> resolveFlowOfMany(link, remoteSource, mapper)
        }
    }

    override fun getAccount(link: LinkDto): Flow<Resource<Account>> {
        val mapper: (AccountDto) -> Account = { it.toAccount() }
        return when (localSource) {
            is LocalSource.Available -> resolveFlow(link, localSource, remoteSource, mapper)
            is LocalSource.Unavailable -> resolveFlow(link, remoteSource, mapper)
        }
    }

    companion object {

        @Volatile
        private var intance: AccountsRepository? = null

        fun getInstance(): AccountsRepository =
            intance ?: throw IllegalStateException("An instance must be build in the app")

        fun buildInstance(
            localSource: LocalSource<LinkDto, AccountDto>,
            remoteSource: RemoteSource<AccountDto>
        ) {
            synchronized(this) {
                if (intance == null) {
                    intance = AccountsRepositoryImpl(
                        localSource = localSource,
                        remoteSource = remoteSource
                    )
                }
            }
        }
    }
}