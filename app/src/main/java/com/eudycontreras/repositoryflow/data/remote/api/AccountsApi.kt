package com.eudycontreras.repositoryflow.data.remote.api

import com.eudycontreras.repositoryflow.data.remote.dto.AccountDto
import com.eudycontreras.repositoryflow.data.sources.RemoteSource

interface AccountsApi: RemoteSource<AccountDto>