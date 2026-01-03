package com.aman.payplit.presentation.settlement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.presentation.group.SharedGroupViewModel
import com.aman.payplit.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(
    navController: NavController,
    viewModel: SharedGroupViewModel // Shared ViewModel provided by NavGraph
) {
    // Collect state from the shared group session
    val state by viewModel.detailState.collectAsState()

    // Local state for Tab selection: 0 = Your Debts, 1 = All Members
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // 🔥 INDUSTRY GRADE: Fetch fresh settlement data from Render when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchSettlementData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Balances", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. TAB ROW (SWITCH BETWEEN PERSONAL AND GLOBAL)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text("Your Debts", modifier = Modifier.padding(vertical = 12.dp))
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text("All Members", modifier = Modifier.padding(vertical = 12.dp))
                    }
                )
            }

            // Determine which list to display
            val currentDisplayList = if (selectedTabIndex == 0) {
                state.personalSummary
            } else {
                state.summary
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // LOADING STATE
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // EMPTY STATE
                else if (currentDisplayList.isEmpty()) {
                    EmptySettlementView()
                }
                // DATA LIST
                else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f), // Pushes the button to the bottom
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(currentDisplayList) { line ->
                                SettlementCard(text = line)
                            }
                        }

                        // 🔥 3. RECORD PAYMENT BUTTON
                        // Visible only on the 'Your Debts' tab and only if you actually owe/are owed
                        if (selectedTabIndex == 0 && currentDisplayList.isNotEmpty()) {
                            Button(
                                onClick = {
                                    // Navigate to Settle Up Screen
                                    navController.navigate(Screen.SettleUp.createRoute(viewModel.groupId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50) // Industry standard Green
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Payments, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Record Cash Payment",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettlementCard(text: String) {
    // Logic: Identify if user is getting money back or paying
    val isReceiving = text.contains("get back", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isReceiving) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = if (isReceiving) Color(0xFF2E7D32) else Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun EmptySettlementView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🎉 All settled up!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "No outstanding debts found in this group.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}