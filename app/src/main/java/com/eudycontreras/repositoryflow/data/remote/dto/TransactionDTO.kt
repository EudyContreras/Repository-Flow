package com.eudycontreras.repositoryflow.data.remote.dto

data class TransactionDTO(
    val id: String?,
    val amount: String?,
    val sourceAccountId: String?,
    val targetAccountId: String?,
    val propertyA: String?,
    val propertyB: String?
) {
    companion object {
        const val TRANSACTION_DTO_REL = "23882y01e39128"
    }
}
