package com.aman.payplit.presentation.group

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.data.remote.dto.*
import com.aman.payplit.domain.repository.GroupRepository
import com.aman.payplit.domain.repository.ItemRepository
import com.aman.payplit.presentation.group_detail.GroupDetailState
import com.aman.payplit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SharedGroupViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val groupRepository: GroupRepository,
    val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    // State for AddItem Step 1 -> Step 2 transition
    var expenseName by mutableStateOf("")
    var totalAmount by mutableStateOf("")

    private val _detailState = MutableStateFlow(GroupDetailState())
    val detailState = _detailState.asStateFlow()

    private val _members = MutableStateFlow<List<UserDto>>(emptyList())
    val members = _members.asStateFlow()

    // Split logic states
    val splitValues = mutableStateListOf<String>()
    val memberSelected = mutableStateListOf<Boolean>()

    // Pagination controls
    private var currentOffset = 0
    private val PAGE_SIZE = 10
    private var isEndReached = false

    init {
        if (groupId.isNotEmpty()) {
            refreshGroupData(isManual = false)
            fetchMembers()
        }
    }

    fun onPayerSelected(uid: String) {
        _detailState.update { it.copy(selectedPayerId = uid) }
    }

    // 1. Add a separate function to fetch just the summary
    fun fetchSummaryOnly() {
        val userId = sessionManager.getUserId() ?: ""
        viewModelScope.launch {
            itemRepository.getSummary(groupId, userId).collect { result ->
                if (result is Resource.Success) {
                    _detailState.update { it.copy(
                        // 🔥 Update ONLY the summary part of the state
                        summary = result.data ?: emptyList()
                    )}
                }
            }
        }
    }
    /**
     * 🔥 INDUSTRY FIX: Refresh data clears existing list and resets offset
     * to prevent "Duplicate Key" crashes.
     */
    fun refreshGroupData(isManual: Boolean = true) {
        val userId = sessionManager.getUserId() ?: ""
        currentOffset = 0
        isEndReached = false

        viewModelScope.launch {
            itemRepository.getGroupData(groupId, userId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _detailState.update { it.copy(
                            isLoading = !isManual && it.items.isEmpty(),
                            isRefreshing = isManual,
                            // ❌ REMOVED: isSuccess = false
                            // We do NOT reset success here, because it kills navigation events
                        )}
                    }
                    is Resource.Success -> {
                        _detailState.update { currentState ->
                            currentState.copy(
                                items = result.data?.first ?: emptyList(),
                                summary = if (result.data?.second?.isNotEmpty() == true) result.data.second else currentState.summary,
                                groupGraph = if (result.data?.third?.isNotEmpty() == true) result.data.third else currentState.groupGraph,
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                        currentOffset = 10
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(isLoading = false, isRefreshing = false, error = result.message) }
                    }
                }
            }
        }
    }

    /**
     * 🔥 INDUSTRY FIX: Pagination deduplication
     */
    fun loadNextItems() {
        // Prevent loading if we are already at the end or currently refreshing
        if (_detailState.value.isLoading || _detailState.value.isPaginationLoading || isEndReached || currentOffset == 0) return

        viewModelScope.launch {
            itemRepository.getItemsPaginated(groupId, PAGE_SIZE, currentOffset).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _detailState.update { it.copy(isPaginationLoading = true) }
                    }
                    is Resource.Success -> {
                        val newItems = result.data ?: emptyList()
                        if (newItems.isEmpty()) {
                            isEndReached = true
                            _detailState.update { it.copy(isPaginationLoading = false) }
                        } else {
                            _detailState.update { currentState ->
                                // Filter out items that are already in the list to prevent Key crash
                                val currentIds = currentState.items.map { it.itemId }.toSet()
                                val uniqueNewItems = newItems.filter { it.itemId !in currentIds }

                                currentState.copy(
                                    items = currentState.items + uniqueNewItems,
                                    isPaginationLoading = false
                                )
                            }
                            currentOffset += PAGE_SIZE
                        }
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(isPaginationLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    private fun fetchMembers() {
        viewModelScope.launch {
            itemRepository.getGroupMembers(groupId).collect { result ->
                if (result is Resource.Success) {
                    val data = result.data ?: emptyList()
                    _members.value = data

                    // Reset Split UI
                    splitValues.clear()
                    splitValues.addAll(List(data.size) { "0" })
                    memberSelected.clear()
                    memberSelected.addAll(List(data.size) { true })

                    if (_detailState.value.selectedPayerId == null) {
                        _detailState.update { it.copy(selectedPayerId = sessionManager.getUserId()) }
                    }
                }
            }
        }
    }

    fun toggleSplitEqually(isEnabled: Boolean, amount: String) {
        if (!isEnabled) return
        val total = amount.toDoubleOrNull() ?: 0.0
        val selectedCount = memberSelected.count { it }
        if (selectedCount > 0) {
            val share = BigDecimal(total / selectedCount).setScale(2, RoundingMode.HALF_UP).toString()
            for (i in memberSelected.indices) {
                splitValues[i] = if (memberSelected[i]) share else "0"
            }
        }
    }

    fun onMemberCheckedChange(index: Int, isSelected: Boolean, isEquallyMode: Boolean, amount: String) {
        memberSelected[index] = isSelected
        if (!isSelected) splitValues[index] = "0"
        if (isEquallyMode) toggleSplitEqually(true, amount)
    }

    fun submitExpense(name: String, amount: String) {
        val request = AddItemRequest(
            itemName = name,
            itemTotalAmount = amount.toDoubleOrNull() ?: 0.0,
            itemPayer = listOf(_detailState.value.selectedPayerId ?: ""),
            itemSpliter = _members.value.map { it.userId },
            itemSpliterValue = splitValues.map { it.toDoubleOrNull() ?: 0.0 },
            itemGroupId = groupId,
            itemDateUpdate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
            itemTimeUpdate = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        )
        viewModelScope.launch {
            itemRepository.createExpense(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _detailState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        // ✅ Step 1: Set Success to trigger UI navigation
                        _detailState.update { it.copy(isSuccess = true, isLoading = false) }

                        // ✅ Step 2: Refresh Metadata
                        fetchSummaryOnly()

                        // ❌ REMOVED: refreshGroupData(isManual = true)
                        // Why? Because GroupDetailScreen already has an ON_RESUME observer
                        // that calls refreshGroupData automatically when you navigate back.
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId).collect { result ->
                when (result) {
                    is Resource.Loading -> {} // Optional loading state
                    is Resource.Success -> {
                        _detailState.update { it.copy(userMessage = "Deleted") }
                        refreshGroupData(isManual = true)
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(error = result.message) }
                    }
                }
            }
        }
    }

    fun addMember(email: String, onShowMessage: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            groupRepository.addMember(groupId, email).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        fetchMembers() // Update the shared member list
                        onSuccess()
                    }
                    is Resource.Error -> onShowMessage(result.message ?: "Error")
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun onMessageShown() {
        _detailState.update { it.copy(userMessage = null) }
    }

    fun resetSuccessState() {
        _detailState.update { it.copy(isSuccess = false) }
    }
    fun fetchSettlementData() {
        val userId = sessionManager.getUserId() ?: ""
        viewModelScope.launch {
            groupRepository.getSettlementSummary(groupId, userId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _detailState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _detailState.update { it.copy(
                            isLoading = false,
                            summary = result.data?.first ?: emptyList(),      // Global list
                            personalSummary = result.data?.second ?: emptyList() // Personal list
                        )}
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun getCreditors(): List<Pair<UserDto, Double>> {
        val currentUserId = sessionManager.getUserId() ?: return emptyList()
        val graph = _detailState.value.groupGraph
        val membersList = _members.value

        // Look at my specific node in the graph
        val myDebts = graph[currentUserId] ?: return emptyList()

        return myDebts.mapNotNull { (otherUserId, amount) ->
            // In your optimal logic, a negative amount means "I owe them"
            if (amount < 0) {
                val user = membersList.find { it.userId == otherUserId }
                if (user != null) user to Math.abs(amount) else null
            } else null
        }
    }

// Inside SharedGroupViewModel.kt

    fun submitPayment(receiverId: String, amount: String, onSuccess: () -> Unit) {
        val userId = sessionManager.getUserId() ?: return
        val amt = amount.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            itemRepository.createPayment(groupId, userId, receiverId, amt).collect { result ->
                when (result) {
                    is Resource.Loading -> _detailState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        // 🔥 INDUSTRY FIX 1: Clear the old summary immediately
                        // This prevents the UI from showing old debt numbers
                        _detailState.update { it.copy(
                            summary = emptyList(),
                            personalSummary = emptyList(),
                            groupGraph = emptyMap()
                        )}

                        // 🔥 INDUSTRY FIX 2: Small delay (500ms)
                        // This allows the Render/Firebase transaction to fully finish
                        // before we ask for the new balances.
                        kotlinx.coroutines.delay(500)

                        // Refresh everything
                        fetchSummaryOnly()
                        fetchSettlementData()

                        // Set success for navigation
                        _detailState.update { it.copy(isSuccess = true, isLoading = false) }

                        onSuccess()
                    }
                    is Resource.Error -> {
                        _detailState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}