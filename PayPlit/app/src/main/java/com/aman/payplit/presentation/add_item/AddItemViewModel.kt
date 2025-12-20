package com.aman.payplit.presentation.add_item

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.data.remote.dto.AddItemRequest
import com.aman.payplit.data.remote.dto.UserDto
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AddItemState(
    val isLoading: Boolean = false,
    val members: List<UserDto> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemState())
    val state = _state.asStateFlow()

    // Grab values passed from Screen 1
    val groupId: String = savedStateHandle.get<String>("groupId") ?: ""
    val expenseName: String = savedStateHandle.get<String>("name") ?: ""
    val totalAmount: String = savedStateHandle.get<String>("price") ?: "0"

    // States for the Checkboxes and Input fields in Step 2
    val splitValues = mutableStateListOf<String>()
    val memberSelected = mutableStateListOf<Boolean>()

    init {
        fetchMembers()
    }

    private fun fetchMembers() {
        viewModelScope.launch {
            repository.getGroupMembers(groupId).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val data = result.data ?: emptyList()
                        _state.value = _state.value.copy(isLoading = false, members = data)

                        // Initialize Split UI states
                        splitValues.clear()
                        splitValues.addAll(List(data.size) { "0" })
                        memberSelected.clear()
                        memberSelected.addAll(List(data.size) { true })
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }.launchIn(this)
        }
    }

    fun onMemberCheckedChange(index: Int, isSelected: Boolean, isEquallyMode: Boolean) {
        memberSelected[index] = isSelected

        if (!isSelected) {
            // 🔥 Industry Fix: If unchecked, force the value to 0
            splitValues[index] = "0"
        }

        if (isEquallyMode) {
            // Recalculate everyone's share if "Split Equally" is active
            toggleSplitEqually(true)
        }
    }


    // Update the toggleSplitEqually to be more robust
    fun toggleSplitEqually(isEnabled: Boolean) {
        if (!isEnabled) return
        val total = totalAmount.toDoubleOrNull() ?: 0.0
        val selectedCount = memberSelected.count { it }
        if (selectedCount > 0) {
            val share = BigDecimal(total / selectedCount)
                .setScale(2, RoundingMode.HALF_UP)
                .toString()

            for (i in memberSelected.indices) {
                // Only assign amount to selected members, others stay "0"
                splitValues[i] = if (memberSelected[i]) share else "0"
            }
        } else {
            // If no one is selected, reset all to 0
            for (i in splitValues.indices) splitValues[i] = "0"
        }
    }

    fun submitExpense() {
        val currentUserId = sessionManager.getUserId() ?: ""

        val request = AddItemRequest(
            itemName = expenseName,
            itemTotalAmount = totalAmount.toDoubleOrNull() ?: 0.0,
            itemPayer = listOf(currentUserId),
            itemSpliter = _state.value.members.map { it.userId },
            itemSpliterValue = splitValues.map { it.toDoubleOrNull() ?: 0.0 },
            itemGroupId = groupId,
            itemDateUpdate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            itemTimeUpdate = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        )

        viewModelScope.launch {
            repository.createExpense(request).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(isSuccess = true, isLoading = false)
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }.launchIn(this)
        }
    }
}