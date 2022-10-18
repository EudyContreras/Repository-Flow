package com.eudycontreras.repositoryflow.domain.repository

import com.eudycontreras.repositoryflow.domain.model.Transaction
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getTransactions(link: LinkDto): Flow<Resource<List<Transaction>>>
}






