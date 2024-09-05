package com.aman.payplit.model

data class UserInfo(
    val userId : String,
    val name : String,
    val mobileNo : String,
    val email : String,
    val groupIds : List<String>
)
