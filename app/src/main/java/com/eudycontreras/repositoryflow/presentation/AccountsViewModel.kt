package com.eudycontreras.repositoryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.domain.repository.AccountsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AccountUIState {
    object Loading: AccountUIState()
    data class Success(val account: Account, val isFromCache: Boolean): AccountUIState()
    data class Failure(val errorMessage: String, val onRetry: () -> Unit): AccountUIState()
}

class AccountsViewModel(
    private val repository: AccountsRepository
): ViewModel() {

    /**
     * We do not want to expose suspend functions from the VM
     */
    fun fetchData(linkDto: LinkDto): StateFlow<AccountUIState> {
        return repository.getAccount(linkDto).map {
            when (it) {
                is Resource.Loading -> AccountUIState.Loading
                is Resource.Success -> AccountUIState.Success(it.data, it.isFromCache)
                is Resource.Failure -> AccountUIState.Failure(resolveErrorMessage(it.error), it.onInvalidate)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountUIState.Loading
        )
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Some error message"
    }

    companion object {
        fun getFactory(
            repository: AccountsRepository
        ) = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountsViewModel(
                    repository = repository
                ) as T
            }
        }
    }
}