package com.aman.payplit.model

data class UserGroups(
    val groupId : String,
    val groupName : String,
    val groupMembers : List<String>,
    val groupItems : List<String>
)

data class GroupMemberDetail(
    val groupMembers : List<UserInfo>
)

data class AddMemberInGroupRequest(
    val groupId : String,
    val memberEmail : String
)