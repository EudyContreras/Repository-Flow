@file:OptIn(ExperimentalLifecycleComposeApi::class)

package com.eudycontreras.repositoryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle as consume
import com.eudycontreras.repositoryflow.data.remote.dto.AccountDTO
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.data.repository.TransactionsRepositoryImpl
import com.eudycontreras.repositoryflow.presentation.*
import com.eudycontreras.repositoryflow.ui.theme.RepositoryFlowTheme
import com.eudycontreras.repositoryflow.utils.app

class MainActivity : ComponentActivity() {
    private val balanceViewModel: BalanceViewModel by viewModels()

    private val accountsViewModel: AccountsViewModel by viewModels {
        AccountsViewModel.getFactory(
            AccountsRepositoryImpl.getInstance()
        )
    }

    private val transactionsViewModel: TransactionsViewModel by viewModels {
        TransactionsViewModel.getFactory(
            sessionHandler = app.sessionHandler,
            repository = TransactionsRepositoryImpl.getInstance()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RepositoryFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   Column(Modifier.padding(20.dp)) {
                       Box {
                           val accountsFlow = remember {
                               accountsViewModel.fetchData(
                                   linkDto = app.sessionHandler.resolveLink(AccountDTO.ACCOUNTS_DTO_REL)
                               )
                           }
                           val accountsState = accountsFlow.consume()
                           when(val state = accountsState.value) {
                               is AccountsUIState.Loading -> Text(text = "Loading")
                               is AccountsUIState.Success -> Text(text = "Success")
                               is AccountsUIState.Failure -> {
                                   Button(onClick = { state.onRetry() }) {
                                       Text(text = "Retry")
                                   }
                               }
                           }
                       }
                       Box {
                           val transactionsState = transactionsViewModel.transactions.consume()
                           when(val state = transactionsState.value) {
                               is TransactionsUIState.Loading -> Text(text = "Loading")
                               is TransactionsUIState.Success -> Text(text = "Success")
                               is TransactionsUIState.Failure -> Text(text = state.errorMessage)
                           }
                       }

                       Text(modifier = Modifier.padding(vertical = 20.dp), text = "Shared Flow Example")

                       Box {
                           val balanceState by balanceViewModel.balance.consume(BalanceUIState.Loading)
                           when(val state = balanceState) {
                               is BalanceUIState.Loading -> Text(text = "Loading")
                               is BalanceUIState.Success -> Text(text = "Success: ${state.balance}")
                               is BalanceUIState.Failure -> {
                                   Button(onClick = { state.onRetry() }) {
                                       Text(text = "Retry")
                                   }
                               }
                           }
                       }
                       Box {
                           val balanceState by balanceViewModel.balance.consume(BalanceUIState.Loading)
                           when(val state = balanceState) {
                               is BalanceUIState.Loading -> Text(text = "Loading")
                               is BalanceUIState.Success -> Text(text = "Success: ${state.balance}")
                               is BalanceUIState.Failure -> {
                                   Button(onClick = { state.onRetry() }) {
                                       Text(text = "Retry")
                                   }
                               }
                           }
                       }
                   }
                }
            }
        }
    }
}