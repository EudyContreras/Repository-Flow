package com.eudycontreras.repositoryflow.domain.model

sealed class Transaction {
    abstract val id: String

    data class Internal(
        override val id: String,
        val amount: String,
        val sourceAccountId: String,
        val targetAccountId: String,
        val propertyA: String
    ): Transaction()

    data class External(
        override val id: String,
        val amount: String,
        val sourceAccountId: String,
        val targetAccountId: String,
        val propertyB: String
    ): Transaction()
}