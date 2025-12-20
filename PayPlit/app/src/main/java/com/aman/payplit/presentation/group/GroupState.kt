package com.aman.payplit.presentation.group

import com.aman.payplit.data.remote.dto.GroupDto

data class GroupState(
    val isLoading: Boolean = false,    // Initial full-screen loading
    val isRefreshing: Boolean = false, // Pull-to-refresh animation state
    val groups: List<GroupDto> = emptyList(),
    val error: String? = null
)