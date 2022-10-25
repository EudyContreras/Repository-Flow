package com.eudycontreras.repositoryflow.utils

sealed class SessionState {
    object LoggedIn: SessionState()
    object LoggedOut: SessionState()
}