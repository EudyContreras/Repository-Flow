package com.eudycontreras.repositoryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.domain.model.Transaction
import com.eudycontreras.repositoryflow.domain.repository.TransactionsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed class TransactionsUIState {
    object Loading : TransactionsUIState()
    data class Unavailable(val errorMessage: String) : TransactionsUIState()
    data class Available(
        val accounts: List<Transaction>,
        val isFromCache: Boolean
    ) : TransactionsUIState()
}

class TransactionsViewModel(
    private val repository: TransactionsRepository
) : ViewModel() {

    val transactions: StateFlow<TransactionsUIState> = repository.getTransactions(
        LinkDto(TransactionDto.TRANSACTION_DTO_REL)
    ).map {
        when (it) {
            is Resource.Loading -> TransactionsUIState.Loading
            is Resource.Failure -> TransactionsUIState.Unavailable(resolveErrorMessage(it.error))
            is Resource.Success -> TransactionsUIState.Available(it.data, it.isFromCache)//
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = TransactionsUIState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Some error message"
    }

    companion object {
        fun getFactory(
            repository: TransactionsRepository
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TransactionsViewModel(
                    repository = repository
                ) as T
            }
        }
    }
}