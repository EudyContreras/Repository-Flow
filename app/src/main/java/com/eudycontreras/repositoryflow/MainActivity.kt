package com.eudycontreras.repositoryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eudycontreras.repositoryflow.data.repository.AccountsRepositoryImpl
import com.eudycontreras.repositoryflow.data.repository.TransactionsRepositoryImpl
import com.eudycontreras.repositoryflow.presentation.AccountsUIState
import com.eudycontreras.repositoryflow.presentation.AccountsViewModel
import com.eudycontreras.repositoryflow.presentation.TransactionsViewModel
import com.eudycontreras.repositoryflow.ui.theme.RepositoryFlowTheme

class MainActivity : ComponentActivity() {
    private val accountsViewModel: AccountsViewModel by viewModels {
        AccountsViewModel.getFactory(
            AccountsRepositoryImpl.getInstance()
        )
    }

    private val transactionsViewModel: TransactionsViewModel by viewModels {
        TransactionsViewModel.getFactory(
            TransactionsRepositoryImpl.getInstance()
        )
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepositoryFlowTheme {
                val accountsState = accountsViewModel.accounts.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    when(val accounts = accountsState.value) {
                        is AccountsUIState.Loading -> Text(text = "Loading")
                        is AccountsUIState.Success -> Text(text = "Success")
                        is AccountsUIState.Failure -> Text(text = "Failure")
                    }
                }
            }
        }
    }
}