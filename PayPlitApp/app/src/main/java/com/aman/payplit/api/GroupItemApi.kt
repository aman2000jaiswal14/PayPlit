package com.aman.payplit.api

import com.aman.payplit.model.GroupItem
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupItemApi {

    @GET("/items")
    suspend fun getAllItems() : List<GroupItem>

    @GET("/items/{itemId}")
    suspend fun getItemById(@Path("itemId") itemId : String) : GroupItem
}