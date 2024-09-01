package com.aman.payplit.api

import com.aman.payplit.model.UserGroups
import retrofit2.http.GET

interface UserGroupsApi {

    @GET("/groups")
    suspend fun getAllGroups() : List<UserGroups>

    @GET("/")
    suspend fun checkConnectivity(): String
}