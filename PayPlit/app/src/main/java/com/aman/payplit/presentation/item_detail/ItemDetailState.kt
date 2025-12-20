package com.aman.payplit.presentation.item_detail

import com.aman.payplit.data.remote.dto.ItemDto
import com.aman.payplit.data.remote.dto.UserDto

data class ItemDetailState(
    val isLoading: Boolean = false,
    val item: ItemDto? = null,
    val members: List<UserDto> = emptyList(), // To map IDs to Names
    val error: String? = null
)