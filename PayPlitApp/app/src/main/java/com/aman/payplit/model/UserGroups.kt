package com.aman.payplit.model



data class GroupMemberDetail(
    val groupMembers : List<UserInfo>
)

data class AddMemberInGroupRequest(
    val groupId : String,
    val memberEmail : String
)


data class UserGroupsResponse(
    val status: String,
    val message: String,
    val data: GroupsData
)

data class GroupsData(
    val groups: List<String>?
)
data class GroupIdRequest(
    val value: String
)

data class UserGroups(
    val groupId: String,
    val groupName: String,
    val groupMembers: List<String>,
    val groupItems: List<String> = emptyList() // optional if backend doesn't send it
)

data class GroupData(
    val group: UserGroups
)

data class GroupResponse(
    val status: String,
    val message: String,
    val data: GroupData
)
data class ExpenseRequest(
    val groupId: String,
    val currentUserId: String
)