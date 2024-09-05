package com.aman.payplit.globalPP

import com.aman.payplit.api.GroupItemApi
import com.aman.payplit.api.UserGroupsApi
import com.aman.payplit.api.UserInfoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppGlobalObj {
//    val auth = FirebaseAuth.getInstance()
    val baseUrl = "http://192.168.29.141:7000"
    val retrofitObj = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val userApiObj = retrofitObj.create(UserInfoApi::class.java)
    val groupApiObj = retrofitObj.create(UserGroupsApi::class.java)
    val itemApiObj = retrofitObj.create(GroupItemApi::class.java)
}