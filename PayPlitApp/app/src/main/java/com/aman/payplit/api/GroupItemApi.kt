package com.aman.payplit.api

import com.aman.payplit.model.GroupItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupItemApi {

    @GET("/items")
    suspend fun getAllItems() : List<GroupItem>

    @GET("/items/{itemId}")
    suspend fun getItemById(@Path("itemId") itemId : String) : GroupItem

    @POST("/items/create")
    suspend fun createItem(@Body groupItem : GroupItem) : Response<ResponseBody>
}