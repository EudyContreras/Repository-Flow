package com.eudycontreras.repositoryflow.mocking

import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import java.util.*


val MockAccounts = listOf(
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

val MockTransactions = listOf(
    TransactionDto(
        id = UUID.randomUUID().toString(),
        amount = "1000",
        sourceAccountId = "someSourceAccountId",
        targetAccountId = "someTargetAccountId",
        propertyA = "Savings Property",
        propertyB = null
    ),
    TransactionDto(
        id = UUID.randomUUID().toString(),
        amount = "1000",
        sourceAccountId = "someSourceAccountId",
        targetAccountId = "someTargetAccountId",
        propertyA = null,
        propertyB = "Debit Property"
    )
)