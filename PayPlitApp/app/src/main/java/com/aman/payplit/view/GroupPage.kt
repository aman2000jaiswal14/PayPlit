package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj
import com.aman.payplit.model.UserGroups
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val groups = remember { mutableStateOf<List<UserGroups>>(emptyList()) }
    val expanded = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Fetch groups on launch
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = AppGlobalObj.groupApiObj.getGroupsByUserId(AppGlobalObj.currentUserId)
                Log.d("GroupPage", "UserGroupsResponse: $response")
                val groupIds = response.data.groups ?: emptyList()

                val fetchedGroups = mutableListOf<UserGroups>()
                for (groupId in groupIds) {
                    try {
                        val groupData = AppGlobalObj.groupApiObj.getGroupDataByGroupId(
                            com.aman.payplit.model.GroupIdRequest(groupId)
                        )
                        groupData.data?.group?.let { fetchedGroups.add(it) }
                    } catch (e: Exception) {
                        Log.e("GroupPage", "Failed to fetch group $groupId: ${e.message}")
                    }
                }

                groups.value = fetchedGroups
                isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                error.value = "Error fetching groups: ${e.message}"
                isLoading.value = false
                Toast.makeText(context, error.value ?: "Unknown error", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Groups", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.purple_500)),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text("LogOut") },
                            onClick = {
                                expanded.value = false
                                AppGlobalObj.currentUserId = ""
//                                val sharedPref = context.getSharedPreferences("PayplitPrefs", Context.MODE_PRIVATE)
//                                sharedPref.edit().remove("currentUserId").apply()
                                navController.navigate("LoginPage") {
                                    popUpTo("LoginPage") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Group") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Group") },
                onClick = { navController.navigate("AddGroupPage") }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(id = R.color.purple_200),
                            colorResource(id = R.color.purple_500)
                        )
                    )
                )
        ) {
            when {
                isLoading.value -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
                error.value != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error.value ?: "Unknown error", color = Color.Red, fontSize = 18.sp)
                }
                groups.value.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No groups available", color = Color.White, fontSize = 18.sp)
                }
                else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(groups.value) { group ->
                        Card(
                            onClick = {
                                AppGlobalObj.currentSelectedGroup = group
                                navController.navigate("GroupItemsPage")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.groupcontainer)
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_group),
                                        contentDescription = "",
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = group.groupName ?: "Unnamed Group",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Members: ${group.groupMembers?.size ?: 0}",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowRight,
                                    contentDescription = "Go",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
