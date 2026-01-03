package com.aman.payplit.presentation.group_detail

import com.aman.payplit.data.remote.dto.ItemDto

/**
 * Represents the UI state for the Group Details screen.
 */
data class GroupDetailState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginationLoading: Boolean = false, // 🔥 Required for bottom loader
    val items: List<ItemDto> = emptyList(),
    val summary: List<String> = emptyList(),
    val personalSummary: List<String> = emptyList(),
    val groupGraph: Map<String, Map<String, Double>> = emptyMap(),
    val error: String? = null,
    val userMessage: String? = null, // 🔥 Required for Toast notifications
    val isSuccess: Boolean = false, // 🔥 Added for navigation
    val selectedPayerId: String? = null // 🔥 Added for payer selection
)