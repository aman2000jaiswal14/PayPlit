package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedItem
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId
import com.aman.payplit.globalPP.AppGlobalObj.userApiObj
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailPage(navController: NavController) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userNames = remember { mutableStateMapOf<String, String>() }

    // ------------- FETCH USER NAMES FOR PAYER + SPLITTERS -------------
    LaunchedEffect(Unit) {
        scope.launch {
            val allUserIds = mutableSetOf<String>()
            allUserIds.addAll(currentSelectedItem.itemPayer)
            allUserIds.addAll(currentSelectedItem.itemSpliter)

            allUserIds.forEach { userId ->
                try {
                    val response = userApiObj.getUserById(userId)
                    val name = response.data?.user?.name ?: userId
                    userNames[userId] = name
                } catch (e: Exception) {
                    userNames[userId] = userId
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                currentUserId=""
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
        }
    ) { padding ->

        // ------------- APPLY SCAFFOLD PADDING HERE (THE FIX) -------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ------------ HEADER GRADIENT BOX ------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF6C63FF), Color(0xFF3F3D56))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentSelectedItem.itemName,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "₹${currentSelectedItem.itemTotalAmount}",
                        color = Color.White,
                        fontSize = 34.sp
                    )
                }
            }

            // ------------ MAIN CARD SECTION ------------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-40).dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    ),
                    elevation = CardDefaults.cardElevation(15.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // --------- ITEM NAME + DATE + TIME ----------
                        Text("Item Name:", color = Color.White.copy(0.7f), fontSize = 16.sp)
                        Text(currentSelectedItem.itemName, color = Color.White, fontSize = 20.sp)

                        Spacer(Modifier.height(12.dp))

                        Text("Paid On:", color = Color.White.copy(0.7f), fontSize = 16.sp)
                        Text(
                            "Date: ${currentSelectedItem.itemDateUpdate}   •   Time: ${currentSelectedItem.itemTimeUpdate}",
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        // --------- PAYER SECTION ----------
                        Text("Paid By:", color = Color.White.copy(0.7f), fontSize = 16.sp)
                        Spacer(Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF6C63FF), RoundedCornerShape(50))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = userNames[currentSelectedItem.itemPayer.first()] ?: "Unknown",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text("Split Details", fontSize = 22.sp, color = Color.White)
                        Spacer(Modifier.height(10.dp))

                        // --------- SPLITTER LIST ----------
                        LazyColumn(
                            modifier = Modifier.heightIn(min = 100.dp, max = 350.dp)
                        ) {
                            itemsIndexed(currentSelectedItem.itemSpliter) { index, userId ->
                                SplitRow(
                                    name = userNames[userId] ?: "Unknown",
                                    amount = currentSelectedItem.itemSpliterValue.getOrElse(index) { "0" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- CHIP COMPONENT ----------------
@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF2C2C2C), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

// ---------------- SPLIT ROW COMPONENT ----------------
@Composable
fun SplitRow(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = Color.White, fontSize = 18.sp)
        Text("₹$amount", color = Color(0xFF6C63FF), fontSize = 18.sp)
    }
}
