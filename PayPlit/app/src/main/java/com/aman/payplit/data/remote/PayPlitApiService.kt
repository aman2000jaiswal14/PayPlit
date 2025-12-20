package com.aman.payplit.data.remote

import com.aman.payplit.data.remote.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PayPlitApiService {

    // ================================
    // 🔥 USERS
    // ================================

    @POST("/users/create")
    suspend fun createUser(@Body request: CreateUserRequest): Response<PayPlitResponse<UserDto>>

    @POST("/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<PayPlitResponse<UserDto>>

    @POST("/users/groups")
    suspend fun getUserGroupIds(@Body userIdMap: Map<String, String>): Response<PayPlitResponse<UserGroupsDto>>

    @GET("/groups")
    suspend fun getAllGroups(): Response<PayPlitResponse<UserGroupsDetailDto>>


    // ================================
    // 🔥 GROUPS
    // ================================

    @POST("/groups/create")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<PayPlitResponse<SingleGroupDto>>

    @POST("/groups/getGroup")
    suspend fun getGroupById(@Body body: Map<String, String>): Response<PayPlitResponse<SingleGroupDto>>

    @POST("/groups/membersDetail")
    suspend fun getGroupMembersDetail(@Body groupId: String): Response<List<UserDto>>

    @PUT("/groups/addMember")
    suspend fun addMemberToGroup(@Body request: AddMemberRequest): Response<Map<String, String>>

    // Combined: Maps to @app.route("/groups/expenseDetail")
    @POST("/groups/expenseDetail")
    suspend fun getGlobalExpenseDetail(@Body groupId: String): Response<ExpenseDetailResponse>

    // Combined: Maps to @app.route("/groups/expenseDetailbyCurrentUser")
    @POST("/groups/expenseDetailbyCurrentUser")
    suspend fun getCurrentUserExpenseDetail(@Body body: Map<String, String>): Response<ExpenseDetailResponse>


    // ================================
    // 🔥 ITEMS (EXPENSES)
    // ================================

    @POST("/items/create")
    suspend fun createItem(@Body request: AddItemRequest): Response<ResponseBody>

    @POST("/groups/items")
    suspend fun getGroupItems(@Body groupId: String): Response<List<ItemDto>>

    @GET("/items/{itemId}")
    suspend fun getItemById(@Path("itemId") itemId: String): Response<PayPlitResponse<SingleItemDto>>

    @DELETE("/items")
    suspend fun deleteItem(@Query("itemId") itemId: String): Response<PayPlitResponse<Unit>>

    // 🔥 Matches @app.route("/groups", methods=["DELETE"])
    @HTTP(method = "DELETE", path = "/groups", hasBody = true)
    suspend fun deleteGroup(
        @Body body: Map<String, String>
    ): Response<PayPlitResponse<Unit>>
}