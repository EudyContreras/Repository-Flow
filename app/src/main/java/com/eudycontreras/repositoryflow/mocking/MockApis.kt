package com.eudycontreras.repositoryflow.mocking

import com.eudycontreras.repositoryflow.data.remote.api.AccountsService
import com.eudycontreras.repositoryflow.data.remote.api.TransactionsService
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDTO
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDTO
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Result
import kotlinx.coroutines.delay as responseDelay
import kotlin.random.Random

class MockAccountApi: AccountsService {
    override suspend fun post(data: AccountDTO) { }

    override suspend fun get(link: LinkDto): Result<AccountDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<AccountDTO>> {
        responseDelay(1500)
        return if(Random.nextBoolean()) {
            Result.Success(MockAccounts)
        } else Result.Failure("Some error")
    }

    private var value: Double = 10000.0

    override suspend fun getTotalBalance(): Result<Double> {
        responseDelay(500)
        return try {
            Result.Success(value)
        } finally {
            value -= 10
        }
    }
}

class MockTransactionsApi: TransactionsService {
    override suspend fun post(data: TransactionDTO) { }

    override suspend fun get(link: LinkDto): Result<TransactionDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<TransactionDTO>> {
        responseDelay(2000)
        return if (Random.nextBoolean()) {
            Result.Success(MockTransactions)
        } else Result.Success(emptyList())
    }
}