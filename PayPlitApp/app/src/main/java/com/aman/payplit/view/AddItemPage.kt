package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj.addExpenseName
import com.aman.payplit.globalPP.AppGlobalObj.addExpensePrice
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId
import android.content.Context


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemPage(navController: NavController) {

    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "menu", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded.value = false
                                currentUserId = ""

                                val sharedPref = context.getSharedPreferences("PayplitPrefs", Context.MODE_PRIVATE)
                                sharedPref.edit().remove("currentUserId").apply()


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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ----------- Top Gradient Header -----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF6C63FF), Color(0xFF3F3D56))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Enter Expense Details",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ----------- Main Card -----------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F1F1F)
                ),
                elevation = CardDefaults.cardElevation(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Expense Detail",
                        color = Color.White,
                        fontSize = 22.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    // ----------- Item Name TextField -----------
                    TextField(
                        value = itemName.value,
                        onValueChange = { itemName.value = it },
                        label = { Text("Expense Name") },
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2C2C2C),
                            unfocusedContainerColor = Color(0xFF2C2C2C),
                            focusedIndicatorColor = Color(0xFF6C63FF),
                            unfocusedIndicatorColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color(0xFF6C63FF),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    // ----------- Price TextField -----------
                    TextField(
                        value = itemPrice.value,
                        onValueChange = { itemPrice.value = it },
                        label = { Text("Expense Amount") },
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2C2C2C),
                            unfocusedContainerColor = Color(0xFF2C2C2C),
                            focusedIndicatorColor = Color(0xFF6C63FF),
                            unfocusedIndicatorColor = Color.White.copy(0.5f),
                            focusedLabelColor = Color(0xFF6C63FF),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(25.dp))

                    // ----------- Add Button -----------
                    Button(
                        onClick = {
                            if (itemName.value.isNotEmpty() && itemPrice.value.isNotEmpty()) {
                                addExpenseName = itemName.value
                                addExpensePrice = itemPrice.value
                                navController.navigate("AddItemDetailPage")
                            } else {
                                Toast.makeText(context, "Enter Name & Price", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .width(180.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        )
                    ) {
                        Text("Add", fontSize = 20.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
