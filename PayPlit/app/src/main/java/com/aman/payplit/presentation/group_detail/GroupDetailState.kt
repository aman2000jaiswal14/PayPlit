package com.aman.payplit.presentation.group_detail

import com.aman.payplit.data.remote.dto.ItemDto

/**
 * Represents the UI state for the Group Details screen.
 */
data class GroupDetailState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false, // For Pull-to-Refresh
    val items: List<ItemDto> = emptyList(),
    val summary: List<String> = emptyList(),
    val error: String? = null,
    val userMessage: String? = null // For "Item Deleted" notification
)