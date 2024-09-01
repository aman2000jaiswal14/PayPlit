package com.aman.payplit.api

import com.aman.payplit.model.GroupItem
import retrofit2.http.GET

interface GroupItemApi {

    @GET("/items")
    suspend fun getAllItems() : List<GroupItem>
}