package com.aman.payplit.presentation.item_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }

        state.item?.let { item ->
            val isPayment = item.itemType == "PAYMENT"
            if (isPayment) {
                // 🔥 PAYMENT RECEIPT VIEW
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Payments, null, modifier = Modifier.size(64.dp), tint = Color(0xFF4CAF50))
                    Text("Cash Payment", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "${item.itemPayerNames.firstOrNull()} paid ${item.itemSpliterNames.firstOrNull()}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Amount: ₹${item.itemTotalAmount}", fontWeight = FontWeight.Bold)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    // 1. Header Card (Receipt Style)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                item.itemName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "₹${item.itemTotalAmount}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "${item.itemDateUpdate} at ${item.itemTimeUpdate}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Split Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    val splitterNames = item.itemSpliterNames
                    val splitterValues = item.itemSpliterValue ?: emptyList()
                    // 2. Split List
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 🔥 We now iterate using itemSpliterNames directly!
                        itemsIndexed(item.itemSpliterNames) { index, name ->
                            val amount = splitterValues.getOrNull(index) ?: 0.0

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    // 🔥 No more .find { it.userId == ... } lookup!
                                    Text(text = name, fontSize = 18.sp)
                                }
                                Text(
                                    text = "₹$amount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}