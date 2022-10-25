package com.eudycontreras.repositoryflow.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Something to keep track of the logged in state of the user
 */
class SessionHandler {
    private val _sessionState: MutableStateFlow<SessionState> = MutableStateFlow(SessionState.LoggedOut)

    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun updateState(state: SessionState) {
        _sessionState.value = state
    }

    fun resolveLink(rel: String): LinkDto = LinkDto("https://someurl.se")
}