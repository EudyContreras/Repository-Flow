package com.eudycontreras.repositoryflow.mocking

import com.eudycontreras.repositoryflow.data.remote.api.AccountsApi
import com.eudycontreras.repositoryflow.data.remote.api.TransactionsApi
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Result
import kotlinx.coroutines.delay as responseDelay
import java.util.UUID
import kotlin.random.Random

class MockAccountApi: AccountsApi {
    override suspend fun post(data: AccountDto) { }

    override suspend fun get(link: LinkDto): Result<AccountDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<AccountDto>> {
        responseDelay(1300)
        return if(Random.nextBoolean()) {
            Result.Success(MockAccounts)
        } else Result.Failure("Some error")
    }
}

class MockTransactionsApi: TransactionsApi {
    override suspend fun post(data: TransactionDto) { }

    override suspend fun get(link: LinkDto): Result<TransactionDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<TransactionDto>> {
        responseDelay(1000)
        return Result.Success(MockTransactions)
    }
}