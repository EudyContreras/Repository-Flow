package com.eudycontreras.repositoryflow.mocking

import com.eudycontreras.repositoryflow.data.remote.api.AccountsApi
import com.eudycontreras.repositoryflow.data.remote.api.TransactionsApi
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Result
import kotlinx.coroutines.delay
import java.util.UUID

class MockAccountApi: AccountsApi {
    override suspend fun post(data: AccountDto) { }

    override suspend fun get(link: LinkDto): Result<AccountDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<AccountDto>> {
        delay(1300)
        return Result.Success(
            listOf(
                AccountDto(
                    id = UUID.randomUUID().toString(),
                    iban = "ashdfasdhf232rhasdfhakh3w23",
                    balance = "1000 sek",
                    propertyA = "Savings Property",
                    propertyB = null
                ),
                AccountDto(
                    id = UUID.randomUUID().toString(),
                    iban = "assd3hdfasdhf232rhasdfhakh3w23",
                    balance = "20000 sek",
                    propertyA = null,
                    propertyB = "Debit Property"
                )
            )
        )
    }
}

class MockTransactionsApi: TransactionsApi {
    override suspend fun post(data: TransactionDto) { }

    override suspend fun get(link: LinkDto): Result<TransactionDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getMany(link: LinkDto): Result<List<TransactionDto>> {
        TODO("Not yet implemented")
    }

}