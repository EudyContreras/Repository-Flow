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

sealed class AccountsUIState {
    object Loading: AccountsUIState()
    data class Success(val accounts: List<Account>, val isFromCache: Boolean): AccountsUIState()
    data class Failure(val errorMessage: String, val onRetry: suspend () -> Unit): AccountsUIState()
}

class AccountsViewModel(
    private val repository: AccountsRepository
): ViewModel() {

    private val _accounts: MutableStateFlow<AccountsUIState> = MutableStateFlow(AccountsUIState.Loading)
    val accounts: StateFlow<AccountsUIState> = _accounts

    init {
        fetchData(LinkDto(TransactionDto.TRANSACTION_DTO_REL))
    }

    fun fetchData(linkDto: LinkDto) {
        viewModelScope.launch {
            repository.getAccounts(linkDto).map {
                when (it) {
                    is Resource.Loading -> AccountsUIState.Loading
                    is Resource.Success -> AccountsUIState.Success(it.data, it.isFromCache)
                    is Resource.Failure -> AccountsUIState.Failure(resolveErrorMessage(it.error)) {

                    }
                }
            }.collectLatest {
                _accounts.emit(it)
            }
        }
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Some error message"
    }

    companion object {
        fun getFactory(
            repository: AccountsRepository
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountsViewModel(
                    repository = repository
                ) as T
            }
        }
    }
}