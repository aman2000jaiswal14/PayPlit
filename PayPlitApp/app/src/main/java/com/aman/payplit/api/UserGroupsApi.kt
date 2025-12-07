package com.aman.payplit.api

import com.aman.payplit.model.AddMemberInGroupRequest
import com.aman.payplit.model.ExpenseRequest
import com.aman.payplit.model.GroupItem
import com.aman.payplit.model.UserGroups
import com.aman.payplit.model.UserInfo
import com.aman.payplit.model.UserGroupsResponse
import com.aman.payplit.model.GroupResponse
import com.aman.payplit.model.GroupIdRequest

import com.aman.payplit.model.GroupExpensesDetail
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserGroupsApi {

    @GET("/groups")
    suspend fun getAllGroups(): List<UserGroups>

    @POST("/groups/getGroup")
    suspend fun getGroupDataByGroupId(@Body request: GroupIdRequest): GroupResponse


    // Updated: return wrapper object
    @POST("/users/groups")
    suspend fun getGroupsByUserId(@Body userId: String): UserGroupsResponse

    @GET("/groups/members/{groupId}")
    suspend fun getAllMembersByGroupId(@Query("groupId") groupId: String): List<String>

    @POST("/groups/membersDetail")
    suspend fun getAllGroupMembersDetail(@Body groupId: String): List<UserInfo>

    @POST("/groups/items")
    suspend fun getAllItemsOfGroups(@Body groupId: String): List<GroupItem>

    @POST("/groups/expenseDetail")
    suspend fun getAllExpenseDetailOfGroups(@Body groupId: String): GroupExpensesDetail

    @POST("/groups/expenseDetailbyCurrentUser")
    suspend fun getCurrentUserExpenseDetailOfGroups(@Body req: ExpenseRequest): GroupExpensesDetail

    @GET("/")
    suspend fun checkConnectivity(): String

    @POST("/groups/create")
    suspend fun createGroup(@Body request: UserGroups): Response<ResponseBody>

    @PUT("/groups/addMember")
    suspend fun addMemberInGroup(@Body addMemberInGroupRequest: AddMemberInGroupRequest): Response<ResponseBody>
}
