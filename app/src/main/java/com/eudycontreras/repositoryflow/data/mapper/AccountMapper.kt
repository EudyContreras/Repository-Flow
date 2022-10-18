package com.eudycontreras.repositoryflow.data.mapper

import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.domain.model.Account

fun AccountDto.toAccount(): Account {
    return when {
        propertyA != null -> Account.Savings(
            id = id.orEmpty(),
            iban = iban.orEmpty(),
            balance = balance.orEmpty(),
            propertyA = propertyA,
        )
        else -> Account.Debit(
            id = id.orEmpty(),
            iban = iban.orEmpty(),
            balance = balance.orEmpty(),
            propertyB = propertyB.orEmpty(),
        )
    }
}