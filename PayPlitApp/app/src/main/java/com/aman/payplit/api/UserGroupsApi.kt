package com.aman.payplit.api

import com.aman.payplit.model.UserGroups
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserGroupsApi {

    @GET("/groups")
    suspend fun getAllGroups() : List<UserGroups>

    @GET("/groups/members/{groupId}")
    suspend fun getAllMembersByGroupId(@Query("groupId") groupId:String) : List<String>

    @GET("/")
    suspend fun checkConnectivity(): String

    @POST("/groups/create")
    suspend fun createGroup(@Body request: UserGroups) : Response<ResponseBody>
}