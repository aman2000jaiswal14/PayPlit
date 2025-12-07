package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedItem
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.globalPP.AppGlobalObj.itemApiObj
import com.aman.payplit.model.ExpenseRequest
import com.aman.payplit.model.GroupItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupItemsPage(navController: NavController) {

    val groupItems = remember { mutableStateOf(emptyList<GroupItem>()) }
    val groupExpenseDetail = remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val expanded = remember { mutableStateOf(false) }

    // ---------------- LOAD ITEMS ---------------- //
    fun loadItems() {
        scope.launch {
            try {
                groupItems.value = groupApiObj.getAllItemsOfGroups(currentSelectedGroup.groupId)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load items: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -------------- INITIAL DATA LOAD -------------- //
    LaunchedEffect(Unit) {
        loadItems()

        // Load expense summary for CURRENT USER
        scope.launch {
            try {
                val req = ExpenseRequest(
                    groupId = currentSelectedGroup.groupId,
                    currentUserId = currentUserId
                )
                val res = groupApiObj.getCurrentUserExpenseDetailOfGroups(req)
                groupExpenseDetail.value = res.expenseDetail
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load expenses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --------------------------------------------------------------------------- UI START -------------------------------------------------------------------------- //
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Items", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Add Member") },
                            onClick = { navController.navigate("AddMemberInGroup") }
                        )
                        DropdownMenuItem(
                            text = { Text("Group Expense") },
                            onClick = { navController.navigate("GroupExpensePage") }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded.value = false
                                currentUserId = ""
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
                text = { Text("Add Expense") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Expense") },
                onClick = { navController.navigate("AddItemPage") }
            )
        },

        floatingActionButtonPosition = FabPosition.End,

        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorResource(R.color.purple_200),
                                colorResource(R.color.purple_500)
                            )
                        )
                    )
            ) {

                Column(modifier = Modifier.fillMaxSize()) {

                    // ------------------------ EXPENSE SUMMARY BOX ------------------------ //
                    if (groupExpenseDetail.value.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.10f)
                            ),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    text = "Your Expense Summary",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                groupExpenseDetail.value.forEach { line ->
                                    Text(
                                        text = "• $line",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }

                    // ------------------------ ITEMS LIST ------------------------ //
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(groupItems.value, key = { it.itemId }) { item ->

                            Card(
                                onClick = {
                                    currentSelectedItem = item
                                    navController.navigate("ItemDetailPage")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.05f)
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    // Item Icon Circle
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6C63FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            item.itemName.firstOrNull()?.uppercase() ?: "I",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Item Details
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            item.itemName,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Total: ₹${item.itemTotalAmount}",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            "Split among: ${item.itemSpliter.size} members",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 14.sp
                                        )
                                    }

                                    // Delete Item Button
                                    IconButton(onClick = {
                                        scope.launch {
                                            try {
                                                val response = itemApiObj.deleteItem(item.itemId)
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                                                    loadItems() // refresh list
                                                } else {
                                                    Toast.makeText(context, "Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red
                                        )
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
