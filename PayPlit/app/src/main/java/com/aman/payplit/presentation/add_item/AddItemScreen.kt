package com.aman.payplit.presentation.add_item

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.payplit.presentation.group.SharedGroupViewModel
import com.aman.payplit.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    groupId: String,
    sharedViewModel: SharedGroupViewModel // 🔥 Received from NavGraph
) {
    // We use local state for typing, then sync to sharedVm on click
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Info", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name (e.g. Pizza)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Total Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && price.isNotEmpty()) {
                        // 🔥 Sync data to the Shared ViewModel session
                        sharedViewModel.expenseName = name
                        sharedViewModel.totalAmount = price

                        // Navigate to Step 2
                        navController.navigate(
                            Screen.AddItemDetail.createRoute(groupId, name, price)
                        )
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Next: Split Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}