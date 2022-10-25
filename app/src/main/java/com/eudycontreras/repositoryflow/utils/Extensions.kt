package com.eudycontreras.repositoryflow.utils

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eudycontreras.repositoryflow.BankApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore

inline fun <T> Flow<T>.launchAndRepeatCollectIn(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(state) {
        collect {
            action(it)
        }
    }
}

val Semaphore.hasPermits: Boolean
    get() = this.availablePermits > 0

inline fun <reified T> invalidationFlow(
   crossinline block: suspend FlowCollector<Resource<T>>.(() -> Unit) -> Unit
) = flow {
    val semaphore = Semaphore(permits = 1, acquiredPermits = 1)
    while(currentCoroutineContext().isActive) {
        block { if (!semaphore.hasPermits) semaphore.release() }
        semaphore.acquire()
    }
}

suspend fun <T> FlowCollector<Resource<T>>.onFailure(
    error: ResourceError,
    onInvalidate: () -> Unit
) {
    emit(Resource.Failure(error, onInvalidate))
}

val Activity.app: BankApplication
    get() = (application as BankApplication)