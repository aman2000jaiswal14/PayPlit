package com.aman.payplit.data.remote

import com.aman.payplit.data.remote.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PayPlitApiService {

    // ================================
    // 🔥 USERS
    // ================================

    @POST("/v1/users/create")
    suspend fun createUser(@Body request: CreateUserRequest): Response<PayPlitResponse<UserDto>>

    @POST("/v1/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<PayPlitResponse<UserDto>>

    @POST("/v1/users/groups")
    suspend fun getUserGroupIds(@Body userIdMap: Map<String, String>): Response<PayPlitResponse<UserGroupsDto>>

    @GET("/v1/groups")
    suspend fun getAllGroups(): Response<PayPlitResponse<UserGroupsDetailDto>>


    // ================================
    // 🔥 GROUPS
    // ================================

    @POST("/v1/groups/create")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<PayPlitResponse<SingleGroupDto>>

    @POST("/v1/groups/getGroup")
    suspend fun getGroupById(@Body body: Map<String, String>): Response<PayPlitResponse<SingleGroupDto>>

    @POST("/v1/groups/membersDetail")
    suspend fun getGroupMembersDetail(@Body groupId: String): Response<List<UserDto>>

    @PUT("/v1/groups/addMember")
    suspend fun addMemberToGroup(
        @Body request: AddMemberRequest
    ): Response<PayPlitResponse<Unit>>

    // Combined: Maps to @app.route("/groups/expenseDetail")
    @POST("/v1/groups/expenseDetail")
    suspend fun getGlobalExpenseDetail(
        @Body groupId: String // 🔥 Change this from Map<String, String> to String
    ): Response<ExpenseDetailResponse>

    // Combined: Maps to @app.route("/groups/expenseDetailbyCurrentUser")
    @POST("/v1/groups/expenseDetailbyCurrentUser")
    suspend fun getCurrentUserExpenseDetail(@Body body: Map<String, String>): Response<ExpenseDetailResponse>


    // ================================
    // 🔥 ITEMS (EXPENSES)
    // ================================

    @POST("/v1/items/create")
    suspend fun createItem(@Body request: AddItemRequest): Response<ResponseBody>

    @POST("/v1/groups/items")
    suspend fun getGroupItems(
        @Body body: Map<String, @JvmSuppressWildcards Any>
        // body will contain: {"groupId": "...", "limit": 10, "offset": 0}
    ): Response<List<ItemDto>>

    @GET("/v1/items/{itemId}")
    suspend fun getItemById(@Path("itemId") itemId: String): Response<PayPlitResponse<SingleItemDto>>

    @DELETE("/v1/items")
    suspend fun deleteItem(@Query("itemId") itemId: String): Response<PayPlitResponse<Unit>>

    // 🔥 Matches @app.route("/groups", methods=["DELETE"])
    @HTTP(method = "DELETE", path = "/v1/groups", hasBody = true)
    suspend fun deleteGroup(
        @Body body: Map<String, String>
    ): Response<PayPlitResponse<Unit>>

    @POST("/v1/payments/create")
    suspend fun createPayment(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<okhttp3.ResponseBody>
}