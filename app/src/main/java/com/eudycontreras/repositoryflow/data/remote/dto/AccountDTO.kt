package com.eudycontreras.repositoryflow.data.remote.dto

data class AccountDTO(
    val id: String?,
    val iban: String?,
    val balance: String?,
    val propertyA : String?,
    val propertyB: String?
) {
    companion object {
        const val ACCOUNTS_DTO_REL = "32938u423u2983"
    }
}
