package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupExpensePage(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val expanded = remember { mutableStateOf(false) }
    val expenseLines = remember { mutableStateOf<List<String>>(emptyList()) }

    // -------------------- Load All Group Expenses --------------------
    fun loadExpenses() {
        scope.launch {
            try {
                val res = AppGlobalObj.groupApiObj.getAllExpenseDetailOfGroups(currentSelectedGroup.groupId)
                expenseLines.value = res.expenseDetail
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load expenses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        loadExpenses()
    }

    // -------------------- UI --------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Expenses", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
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

                    if (expenseLines.value.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses yet",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(expenseLines.value) { line ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.10f)
                                    ),
                                    elevation = CardDefaults.cardElevation(6.dp)
                                ) {
                                    Text(
                                        text = line,
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
