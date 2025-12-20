package com.aman.payplit.presentation.add_group

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

data class AddGroupState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AddGroupState())
    val state = _state.asStateFlow()

    fun createGroup(groupName: String) {
        if (groupName.isBlank()) {
            _state.value = AddGroupState(error = "Group name cannot be empty")
            return
        }

        val currentUserId = sessionManager.getUserId() ?: ""

        viewModelScope.launch {
            // Initial members list only contains the creator
            repository.createGroup(groupName, listOf(currentUserId)).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = AddGroupState(isLoading = true)
                    is Resource.Success -> _state.value = AddGroupState(isSuccess = true)
                    is Resource.Error -> _state.value = AddGroupState(error = result.message)
                }
            }.launchIn(this)
        }
    }
}