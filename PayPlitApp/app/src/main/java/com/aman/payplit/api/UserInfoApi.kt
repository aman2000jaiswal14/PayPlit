package com.aman.payplit.api

import com.aman.payplit.model.UserInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserInfoApi {

    @POST("/users/create")
    suspend fun createUser(@Body request : UserInfo) : Response<ResponseBody>


}