package com.eudycontreras.repositoryflow.data.remote.api

import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDto
import com.eudycontreras.repositoryflow.data.sources.RemoteSource

interface TransactionsApi: RemoteSource<TransactionDto>