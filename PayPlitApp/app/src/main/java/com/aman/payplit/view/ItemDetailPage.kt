package com.aman.payplit.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.api.GroupItemApi
import com.aman.payplit.api.UserGroupsApi
import com.aman.payplit.model.GroupItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailPage(navController: NavController) {
    val itemInfo = remember {
        mutableStateOf(
            GroupItem(
                itemId = "",
                itemName = "",
                itemGroupId = "",
                itemDateUpdate = "",
                itemTimeUpdate = "",
                itemTotalAmount = "",
                itemPayer = emptyList(),
                itemSpliter = emptyList(),
                itemSpliterValue = emptyList()
            )
        )
    }

    val allGroupMembers = remember {
        mutableStateOf(emptyList<String>())
    }
    val itemPrice = remember {
        mutableStateOf("")
    }
    val itemId = "-O5ihfceUP03VSytLv6g"
    val baseUrlItem = "http://192.168.29.141:7000"
    val retrofitGroupItem = Retrofit.Builder().baseUrl(baseUrlItem).addConverterFactory(
        GsonConverterFactory.create()
    ).build()
    val groupItemApi = retrofitGroupItem.create(GroupItemApi::class.java)
    val userGroupApi = retrofitGroupItem.create(UserGroupsApi::class.java)
    val myContext = LocalContext.current
    val flagLoaded = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        try {
            val fetchedItem = groupItemApi.getItemById(itemId)
            itemInfo.value = fetchedItem
            itemPrice.value = itemInfo.value.itemTotalAmount
//            val fetchedMembers = userGroupApi.getAllMembersByGroupId("-O5hFiaT4XQ1rQwwLRHz")
//            allGroupMembers.value = fetchedMembers
//            if(itemInfo.value.itemId != "" && allGroupMembers.value.size>0) {
                flagLoaded.value = true
//            }

        } catch (e: Exception) {
            println("ItemPage Error fetching groups: ${e.message}")
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Item Info Page", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                )
            )
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                if (flagLoaded.value) {
                    Column(modifier = Modifier.padding(9.dp)) {
                        Text(text = itemInfo.value.itemName, color = Color.White, fontSize = 30.sp)
                        Row(
                            modifier = Modifier
                                .padding(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Price : ", color = Color.White, fontSize = 25.sp)
                            TextField(
                                value = itemPrice.value,
                                onValueChange = { totalPrice ->
                                    itemPrice.value = totalPrice },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.LightGray,
                                    focusedTextColor = Color.Blue,
                                    focusedIndicatorColor = Color.Blue,
                                    unfocusedIndicatorColor = Color.Gray
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Paid By : ", color = Color.White, fontSize = 25.sp)
                            Text(text = itemInfo.value.itemPayer[0], color = Color.White, fontSize = 25.sp)
                        }

                        Text(text = "Date : ${itemInfo.value.itemDateUpdate} Time : ${itemInfo.value.itemTimeUpdate}}", color = Color.White, fontSize = 20.sp)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(7.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.groupcontainer)
                            ),
                            elevation = CardDefaults.cardElevation(7.dp),
                            border = BorderStroke(2.dp, Color.Red)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_label),
                                    contentDescription = ""
                                )
                                LazyColumn() {
                                    items(count = itemInfo.value.itemSpliter.size,
                                        itemContent = { index ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = itemInfo.value.itemSpliter[index], color = Color.White, fontSize = 20.sp)
                                                Text(text = itemInfo.value.itemSpliterValue[index], color = Color.White, fontSize = 20.sp)

                                            }

                                        })
//                                    Text(text = , color = Color.Blue, fontSize = 25.sp)

                                }


                            }

                        }
                    }
                }
            }
        }
    )
}