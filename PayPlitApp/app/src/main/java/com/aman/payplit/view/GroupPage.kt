package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.aman.payplit.globalPP.AppGlobalObj.auth
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.globalPP.AppGlobalObj.userApiObj
import com.aman.payplit.model.UserGroups
import kotlinx.coroutines.launch
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val groups = remember {
        mutableStateOf(emptyList<UserGroups>())
    }
    val expanded = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            try {
                val groupIds: List<String> =
                    userApiObj.getGroupsByUserId(auth.currentUser?.uid.toString())
                val fetchedGroups = mutableListOf<UserGroups>()
                groupIds.forEach { groupId ->
                    val groupData: UserGroups = groupApiObj.getGroupDataByGroupId(groupId)
                    fetchedGroups.add(groupData)
                }
                groups.value = fetchedGroups
                isLoading.value = false
            } catch (e: Exception) {
                println("GroupPage Error fetching groups: ${e.message}")
                error.value = "Error fetching groups: ${e.message}"
                isLoading.value = false
            }

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Groups", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "More Options")
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "LogOut") },
                            onClick = {
                                expanded.value = false
                                auth.signOut()
                                navController.navigate("LoginPage"){
                                    popUpTo("LoginPage"){inclusive = true}
                                }

                            },

                            )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Group") },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Group") },
                onClick = {
                    navController.navigate("AddGroupPage")
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        content = {padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (isLoading.value) {
                    // Show loading indicator
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error.value != null) {
                    // Show error message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = error.value ?: "Unknown error", color = Color.Red)
                    }
                } else if (groups.value.isNotEmpty()) {
                    LazyColumn {
                        items(groups.value) { group ->
                            Card(
                                onClick = {
                                    currentSelectedGroup = group
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
                                border = BorderStroke(2.dp, Color.Red)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Image(painter = painterResource(id = R.drawable.ic_group), contentDescription = "")
                                    Text(text = group.groupName, color = Color.Blue, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Show empty state
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No groups available")
                    }
                }
            }
        }
    )
}