package com.eudycontreras.repositoryflow.data.remote.api

import com.eudycontreras.repositoryflow.data.remote.dto.TransactionDTO
import com.eudycontreras.repositoryflow.data.sources.RemoteSource

interface TransactionsService: RemoteSource<TransactionDTO>