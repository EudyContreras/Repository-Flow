package com.eudycontreras.repositoryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.domain.repository.AccountsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import kotlinx.coroutines.flow.*

sealed class AccountsUIState {
    object Loading: AccountsUIState()
    data class Success(val accounts: List<Account>, val isFromCache: Boolean): AccountsUIState()
    data class Failure(val errorMessage: String, val onRetry: () -> Unit): AccountsUIState()
}

class AccountsViewModel(
    private val repository: AccountsRepository
): ViewModel() {

    fun fetchData(linkDto: LinkDto): StateFlow<AccountsUIState> {
        return repository.getAccounts(linkDto).map {
            when (it) {
                is Resource.Loading -> AccountsUIState.Loading
                is Resource.Success -> AccountsUIState.Success(it.data, it.isFromCache)
                is Resource.Failure -> AccountsUIState.Failure(resolveErrorMessage(it.error), it.onInvalidate)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountsUIState.Loading
        )
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Failure" // Build from the error
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