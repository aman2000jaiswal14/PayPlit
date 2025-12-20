package com.aman.payplit.presentation.settlement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettlementState(
    val isLoading: Boolean = false,
    val globalSummary: List<String> = emptyList(),
    val personalSummary: List<String> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0 // 0 for 'You', 1 for 'Everyone'
)

@HiltViewModel
class SettlementViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SettlementState())
    val state = _state.asStateFlow()

    private val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    init {
        loadSettlements()
    }

    fun loadSettlements() {
        val userId = sessionManager.getUserId() ?: ""
        viewModelScope.launch {
            repository.getSettlementSummary(groupId, userId).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            personalSummary = result.data?.second ?: emptyList(),
                            globalSummary = result.data?.first ?: emptyList()
                        )
                    }
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }
}