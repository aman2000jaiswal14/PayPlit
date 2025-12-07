package com.aman.payplit.model

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)
data class DeleteItemRequest(val itemId: String)

data class GroupItem(
    val itemId : String,
    val itemName : String,
    val itemGroupId : String,
    val itemDateUpdate: String,
    val itemTimeUpdate: String,
    val itemTotalAmount: String,
    val itemPayer : List<String>,
    val itemSpliter : List<String>,
    val itemSpliterValue : List<String>
)

data class GroupExpensesDetail(
    val expenseDetail: List<String>
)
