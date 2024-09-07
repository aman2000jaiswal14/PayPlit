package com.aman.payplit.view

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.api.GroupItemApi
import com.aman.payplit.globalPP.AppGlobalObj.auth
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedItem
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.model.GroupItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupItemsPage(navController: NavController){
    val groupItems = remember{
        mutableStateOf(emptyList<GroupItem>())
    }
    val myContext = LocalContext.current
    val expanded = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        try {
            val fetchedItems = groupApiObj.getAllItemsOfGroups(currentSelectedGroup.groupId)
            groupItems.value = fetchedItems

        }catch (e : Exception)
        {
            println("ItemPage Error fetching groups: ${e.message}")
        }
    }
    
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Item Page", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "More Options")
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Add Member") },
                            onClick = {
                                navController.navigate("AddMemberInGroup")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "LogOut") },
                            onClick = {
                                expanded.value = false
                                auth.signOut()
                                navController.navigate("LoginPage"){
                                    popUpTo("LoginPage"){inclusive = true}
                                }

                            }
                            )
                    }
                })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Expense") },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Expense") },
                onClick = {
                    navController.navigate("AddItemPage")
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        content = {
            Column(modifier = Modifier.padding(it)) {
                if (groupItems.value.isNotEmpty()) {
                    LazyColumn {
                        items(groupItems.value) { item ->
                            Card(
                                onClick = {
                                    currentSelectedItem = item
                                    navController.navigate("ItemDetailPage")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
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
                                    Image(painter = painterResource(id = R.drawable.ic_label), contentDescription ="")
                                    Column {
                                        Text(text = item.itemName, color = Color.Blue, fontSize = 20.sp)
                                        Text(text = "Price : "+item.itemTotalAmount, color = Color.Blue, fontSize = 20.sp)
                                    }
                                    Column {
                                        Text(text = item.itemSpliterValue[0], color = Color.Red, fontSize = 20.sp)
                                        Text(text = "Date : "+item.itemDateUpdate, color = Color.Blue, fontSize = 20.sp)
                                    }

                                }

                            }
                        }
                    }
                }



            }
        }
    )

}