package com.aman.payplit.presentation.item_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailState())
    val state = _state.asStateFlow()

    private val itemId: String = savedStateHandle.get<String>("itemId") ?: ""

    init {
        loadItemDetails()
    }

    private fun loadItemDetails() {
        viewModelScope.launch {
            repository.getItemDetails(itemId).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val item = result.data
                        _state.value = _state.value.copy(item = item, isLoading = false)

                        // After getting item, fetch group members to show names
                        item?.itemGroupId?.let { fetchMembers(it) }
                    }
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }.launchIn(this)
        }
    }

    private fun fetchMembers(groupId: String) {
        viewModelScope.launch {
            repository.getGroupMembers(groupId).collect { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(members = result.data ?: emptyList())
                }
            }
        }
    }
}