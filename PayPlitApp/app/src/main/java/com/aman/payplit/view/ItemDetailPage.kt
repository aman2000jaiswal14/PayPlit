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
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedItem
import com.aman.payplit.model.GroupItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailPage(navController: NavController) {
    val myContext = LocalContext.current
    val flagLoaded = remember {
        mutableStateOf(true)
    }
    val myItem = currentSelectedItem
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
                        Text(text = currentSelectedItem.itemName, color = Color.White, fontSize = 30.sp)
                        Row(
                            modifier = Modifier
                                .padding(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Price : ", color = Color.White, fontSize = 25.sp)
                            Text(text = currentSelectedItem.itemTotalAmount, color = Color.White, fontSize = 25.sp)

                        }

                        Row(
                            modifier = Modifier
                                .padding(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "Paid By : ", color = Color.White, fontSize = 25.sp)
                            Text(text = currentSelectedItem.itemPayer[0], color = Color.White, fontSize = 25.sp)
                        }

                        Text(text = "Date : ${currentSelectedItem.itemDateUpdate} Time : ${currentSelectedItem.itemTimeUpdate}}", color = Color.White, fontSize = 20.sp)
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
                                    items(count = currentSelectedItem.itemSpliter.size,
                                        itemContent = { index ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = currentSelectedItem.itemSpliter[index], color = Color.White, fontSize = 20.sp)
                                                Text(text = currentSelectedItem.itemSpliterValue[index], color = Color.White, fontSize = 20.sp)

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