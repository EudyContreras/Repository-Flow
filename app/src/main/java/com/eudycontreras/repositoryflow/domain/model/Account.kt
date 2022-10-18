package com.eudycontreras.repositoryflow.domain.model

sealed class Account {
    abstract val id: String

    data class Savings(
        override val id: String,
        val iban: String,
        val balance: String,
        val propertyA: String
    ): Account()

    data class Debit(
        override val id: String,
        val iban: String,
        val balance: String,
        val propertyB: String
    ): Account()
}