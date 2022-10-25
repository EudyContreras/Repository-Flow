package com.eudycontreras.repositoryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

sealed class AccountsUIState {
    object Loading: AccountsUIState()
    data class Success(val accounts: List<Account>, val isFromCache: Boolean): AccountsUIState()
    data class Failure(val errorMessage: String, val onRetry: () -> Unit): AccountsUIState()
}

class AccountsSharedViewModel(
    private val someStaticLinkDto: LinkDto
): ViewModel() {

    val accounts: SharedFlow<AccountsUIState> by lazy {
        val repository = AccountsRepositoryImpl.getInstance()
        repository.getAccounts(someStaticLinkDto).map {
            when (it) {
                is Resource.Loading -> AccountsUIState.Loading
                is Resource.Failure -> AccountsUIState.Failure(resolveErrorMessage(it.error), it.onInvalidate)
                is Resource.Success -> AccountsUIState.Success(it.data, it.isFromCache)
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily
        )
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Failure" // Build from the error
    }

    companion object {
        fun getFactory(
            linkDto: LinkDto
        ) = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountsSharedViewModel(
                    someStaticLinkDto = linkDto
                ) as T
            }
        }
    }
}