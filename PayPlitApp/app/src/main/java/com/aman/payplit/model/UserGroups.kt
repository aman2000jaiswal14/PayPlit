package com.aman.payplit.model

data class UserGroups(
    val groupId : String,
    val groupName : String,
    val groupMembers : List<String>
)
