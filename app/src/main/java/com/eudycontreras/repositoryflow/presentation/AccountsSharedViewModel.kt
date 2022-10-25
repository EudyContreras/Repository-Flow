package com.eudycontreras.repositoryflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.domain.repository.AccountsRepository
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import com.eudycontreras.repositoryflow.utils.ResourceError
import kotlinx.coroutines.flow.*

class AccountsSharedViewModel(
    private val someStaticLinkDto: LinkDto
): ViewModel() {

    val accounts: SharedFlow<AccountsUIState> by lazy {
        val repository = AccountsRepositoryImpl.getInstance()
        repository.getAccounts(someStaticLinkDto).map {
            when (it) {
                is Resource.Loading -> AccountsUIState.Loading
                is Resource.Failure -> AccountsUIState.Failure(resolveErrorMessage(it.error), it.onInvalidate)
                is Resource.Success -> AccountsUIState.Success(it.data, it.isFromCache)//
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Some error message"
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