package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.api.UserGroupsApi
import com.aman.payplit.model.UserGroups
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPage(navController: NavController) {
    val baseUrlUserGroups = "http://192.168.29.141:7000"
    val retrofitUserGroup = Retrofit.Builder().baseUrl(baseUrlUserGroups)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val userGroupApi = retrofitUserGroup.create(UserGroupsApi::class.java)

    val groups = remember {
        mutableStateOf(emptyList<UserGroups>())
    }
    val myContext = LocalContext.current


    LaunchedEffect(key1 = Unit) {
        try {

            val fetchedGroups = userGroupApi.getAllGroups()
            groups.value = fetchedGroups
        } catch (e: Exception) {

            println("GroupPage Error fetching groups: ${e.message}")
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Groups", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                )
            )
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                if (groups.value.isNotEmpty()) {
                    LazyColumn {
                        items(groups.value) { group ->
                            Card(
                                onClick = {
                                    navController.navigate("GroupItemsPage")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(7.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.groupcontainer)
                                ),
                                elevation = CardDefaults.cardElevation(7.dp),
                                border = BorderStroke(2.dp,Color.Red)
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Image(painter = painterResource(id = R.drawable.ic_group), contentDescription ="")
                                    Text(text = group.groupName, color = Color.Blue, fontSize = 20.sp)
                                }

                            }
                        }
                    }
                }
            }
        }

    )
}