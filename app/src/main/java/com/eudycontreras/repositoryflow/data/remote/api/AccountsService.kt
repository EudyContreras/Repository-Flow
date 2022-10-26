package com.eudycontreras.repositoryflow.data.remote.api

import com.eudycontreras.repositoryflow.data.remote.dto.AccountDTO
import com.eudycontreras.repositoryflow.data.sources.RemoteSource
import com.eudycontreras.repositoryflow.utils.Result

interface AccountsService: RemoteSource<AccountDTO> {
    suspend fun getTotalBalance(): Result<Double>
}