package com.aman.payplit.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
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
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(GroupState())
    val state = _state.asStateFlow()

    init {
        loadGroups(isPullToRefresh = false)
    }

    fun loadGroups(isPullToRefresh: Boolean = true) {
        val userId = sessionManager.getUserId() ?: return

        viewModelScope.launch {
            repository.getGroups(userId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (isPullToRefresh) {
                            _state.value = _state.value.copy(isRefreshing = true)
                        } else {
                            _state.value = _state.value.copy(isLoading = true)
                        }
                    }
                    is Resource.Success -> {
                        _state.value = GroupState(
                            groups = result.data ?: emptyList(),
                            isLoading = false,
                            isRefreshing = false
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

    fun logout() {
        sessionManager.logout()
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