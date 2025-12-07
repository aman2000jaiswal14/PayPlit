package com.aman.payplit.api

import com.aman.payplit.model.UserInfo
import com.aman.payplit.model.UserResponseWrapper
import com.aman.payplit.model.LoginResponse
import com.aman.payplit.model.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.GET

interface UserInfoApi {

    @POST("/users/create")
    suspend fun createUser(@Body request: UserInfo): Response<ResponseBody>

    @POST("/users/login")
    suspend fun loginUser(@Body loginRequest: Map<String, String>): Response<LoginResponse>

    @POST("/users/groups")
    suspend fun getGroupsByUserId(@Body userId: String): List<String>

    @GET("/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): ApiResponse<UserResponseWrapper>
}
