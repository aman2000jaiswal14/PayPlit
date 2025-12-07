package com.aman.payplit.model
data class UserResponseWrapper(
    val user: UserInfo
)
data class UserInfo(
    val userId : String,
    val name : String,
    val mobileNo : String,
    val email : String,
    val password: String, // <- add this
    val groupIds : List<String>
)

data class UserIdNameVal(
    val userId: String,
    val name : String,
    var money : String
)
data class LoginResponse(
    val status: String,
    val message: String,
    val data: UserIdData?
)

data class UserIdData(
    val userId: String
)