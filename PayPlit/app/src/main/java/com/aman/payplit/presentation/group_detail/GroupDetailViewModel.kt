package com.aman.payplit.presentation.group_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(GroupDetailState())
    val state = _state.asStateFlow()

    val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    init {
        refreshData(isManual = false)
    }

    fun refreshData(isManual: Boolean = true) {
        val userId = sessionManager.getUserId() ?: ""
        viewModelScope.launch {
            itemRepository.getGroupData(groupId, userId).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (isManual) _state.value = _state.value.copy(isRefreshing = true)
                        else _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            items = result.data?.first ?: emptyList(),
                            summary = result.data?.second ?: emptyList(),
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, isRefreshing = false, error = result.message)
                    }
                }
            }.launchIn(this)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(userMessage = "Item deleted successfully")
                        refreshData(isManual = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = "Delete failed")
                    }
                    else -> {}
                }
            }.launchIn(this)
        }
    }

    // Call this to clear the message after showing it
    fun onMessageShown() {
        _state.value = _state.value.copy(userMessage = null)
    }
}