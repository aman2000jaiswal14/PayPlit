package com.aman.payplit.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.data.local.dao.ExpenseDao
import com.aman.payplit.data.local.dao.GroupDao
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val sessionManager: SessionManager,
    private val groupDao: GroupDao,    // 🔥 Add this
    private val expenseDao: ExpenseDao  // 🔥 Add this
) : ViewModel() {

    private val _state = MutableStateFlow(GroupState())
    val state = _state.asStateFlow()

    init {
        loadGroups(isPullToRefresh = false)
    }

// In GroupViewModel.kt

    fun loadGroups(isPullToRefresh: Boolean = false) {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            repository.getGroups(userId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        // 🔥 MUTUAL EXCLUSION LOGIC
                        if (_state.value.groups.isEmpty()) {
                            // If list is empty, show center spinner, hide refresh spinner
                            _state.value = _state.value.copy(isLoading = true, isRefreshing = false)
                        } else {
                            // If we have data, hide center spinner, show refresh spinner
                            _state.value = _state.value.copy(isRefreshing = true, isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            groups = result.data ?: emptyList(),
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. Clear Local Database (Security First)
                groupDao.clearAll()
                expenseDao.clearAll()

                // 2. Clear Session (UserId)
                sessionManager.logout()

                // 3. Trigger UI navigation
                onComplete()
            } catch (e: Exception) {
                // Log error if cleanup fails
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroup(groupId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        // Refresh the list immediately
                        loadGroups(isPullToRefresh = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                    }
                    else -> {}
                }
            }.launchIn(this)
        }
    }
}