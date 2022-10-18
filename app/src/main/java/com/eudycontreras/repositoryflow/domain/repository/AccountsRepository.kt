package com.eudycontreras.repositoryflow.domain.repository

import com.eudycontreras.repositoryflow.domain.model.Account
import com.eudycontreras.repositoryflow.utils.LinkDto
import com.eudycontreras.repositoryflow.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAccounts(link: LinkDto): Flow<Resource<List<Account>>>
    fun getAccount(link: LinkDto): Flow<Resource<Account>>
}



