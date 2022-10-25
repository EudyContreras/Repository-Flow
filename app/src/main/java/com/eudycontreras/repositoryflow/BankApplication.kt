package com.eudycontreras.repositoryflow

import android.app.Application
import com.eudycontreras.repositoryflow.data.local.MemoryCache
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.data.repository.TransactionsRepositoryImpl
import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.mocking.MockAccountApi
import com.eudycontreras.repositoryflow.mocking.MockTransactionsApi
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.SessionHandler
import com.eudycontreras.repositoryflow.utils.SessionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BankApplication: Application() {
    /**
     * Something that triggers post logged out and post logged in flows.
     * Imagine this comes from some form of Session Handler
     */
    val sessionHandler: SessionHandler = SessionHandler()

    override fun onCreate() {
        super.onCreate()

        /**
         * In some scenarios we may have some shared memory cache
         */
        val memoryCache: LocalSource.KeyValue<LinkDto, AccountDto> = MemoryCache()

        /**
         * A scope we can use at app level. Can be used for repositories too.
         */
        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

        applicationScope.launch {
            sessionHandler.sessionState.collectLatest { state ->
                if (state is SessionState.LoggedOut) {
                    memoryCache.clear()
                }
            }
        }

        AccountsRepositoryImpl.buildInstance(
            localSource = memoryCache,
            remoteSource = MockAccountApi()
        )

        TransactionsRepositoryImpl.buildInstance(
            remoteSource = MockTransactionsApi()
        )
    }
}