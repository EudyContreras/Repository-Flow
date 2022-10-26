package com.eudycontreras.repositoryflow.mocking

import com.eudycontreras.repositoryflow.data.remote.dto.AccountDTO
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDTO
import java.util.*


val MockAccounts = listOf(
    AccountDTO(
        id = UUID.randomUUID().toString(),
        iban = "some_iban_id",
        balance = "1300",
        propertyA = "Savings Property",
        propertyB = null
    ),
    AccountDTO(
        id = UUID.randomUUID().toString(),
        iban = "some_iban_id",
        balance = "20000",
        propertyA = null,
        propertyB = "Debit Property"
    )
)

val MockTransactions = listOf(
    TransactionDTO(
        id = UUID.randomUUID().toString(),
        amount = "1000",
        sourceAccountId = "some_account_id",
        targetAccountId = "some_account_id",
        propertyA = "Internal Transaction Property",
        propertyB = null
    ),
    TransactionDTO(
        id = UUID.randomUUID().toString(),
        amount = "1000",
        sourceAccountId = "some_account_id",
        targetAccountId = "some_account_id",
        propertyA = null,
        propertyB = "External Transaction Property"
    )
)