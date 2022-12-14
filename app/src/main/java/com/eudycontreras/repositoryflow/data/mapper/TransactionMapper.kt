package com.eudycontreras.repositoryflow.data.mapper

import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDTO
import com.eudycontreras.repositoryflow.domain.model.Transaction

fun TransactionDTO.toTransaction(): Transaction {
    return when {
        propertyA != null -> Transaction.Internal(
            id = id.orEmpty(),
            amount = amount.orEmpty(),
            sourceAccountId = sourceAccountId.orEmpty(),
            targetAccountId = targetAccountId.orEmpty(),
            propertyA = propertyA,
        )
        else -> Transaction.External(
            id = id.orEmpty(),
            amount = amount.orEmpty(),
            sourceAccountId = sourceAccountId.orEmpty(),
            targetAccountId = targetAccountId.orEmpty(),
            propertyB = propertyB.orEmpty(),
        )
    }
}