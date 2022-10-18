package com.eudycontreras.repositoryflow

import android.app.Application
import com.eudycontreras.repositoryflow.data.local.MemoryCache
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.data.repository.TransactionsRepositoryImpl
import com.eudycontreras.repositoryflow.data.sources.LocalSource
import com.eudycontreras.repositoryflow.mocking.MockAccountApi
import com.eudycontreras.repositoryflow.mocking.MockTransactionsApi
import com.eudycontreras.repositoryflow.utils.LinkDto

class BankApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        AccountsRepositoryImpl.buildInstance(
            localSource = MemoryCache(),
            remoteSource = MockAccountApi()
        )
        TransactionsRepositoryImpl.buildInstance(
            localSource = object : LocalSource.Unavailable<LinkDto, TransactionDto> {},
            remoteSource = MockTransactionsApi()
        )
    }
}