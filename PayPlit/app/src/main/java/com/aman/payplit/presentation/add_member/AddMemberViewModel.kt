package com.aman.payplit.presentation.add_member

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddMemberState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val repository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddMemberState())
    val state = _state.asStateFlow()

    private val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    fun onAddMember(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _state.value = AddMemberState(error = "Enter a valid email address")
            return
        }

        viewModelScope.launch {
            repository.addMember(groupId, email).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = AddMemberState(isLoading = true)
                    is Resource.Success -> _state.value = AddMemberState(isSuccess = true)
                    is Resource.Error -> _state.value = AddMemberState(error = result.message)
                }
            }.launchIn(this)
        }
    }
}