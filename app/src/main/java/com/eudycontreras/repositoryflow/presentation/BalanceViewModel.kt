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

sealed class BalanceUIState {
    object Loading: BalanceUIState()
    data class Success(val balance: Double, val isFromCache: Boolean): BalanceUIState()
    data class Failure(val errorMessage: String, val onRetry: () -> Unit): BalanceUIState()
}

class BalanceViewModel: ViewModel() {

    val balance: SharedFlow<BalanceUIState> by lazy {
        val repository = AccountsRepositoryImpl.getInstance()
        repository.getTotalBalance().map {
            when (it) {
                is Resource.Loading -> BalanceUIState.Loading
                is Resource.Failure -> BalanceUIState.Failure(resolveErrorMessage(it.error), it.onInvalidate)
                is Resource.Success -> BalanceUIState.Success(it.data, it.isFromCache)
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily
        )
    }

    private fun resolveErrorMessage(failure: ResourceError): String {
        return "Some error message"
    }
}