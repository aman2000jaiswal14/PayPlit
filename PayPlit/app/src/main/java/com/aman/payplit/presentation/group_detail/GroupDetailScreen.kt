package com.aman.payplit.presentation.group_detail

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.aman.payplit.data.remote.dto.ItemDto
import com.aman.payplit.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    navController: NavController,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var itemToDelete by remember { mutableStateOf<ItemDto?>(null) }

    // Lifecycle observer to refresh when returning
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) { viewModel.refreshData(isManual = true) }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 🔥 Show Success Message
    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    // --- DELETE DIALOG ---
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Expense?") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.itemName}'?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.let { viewModel.deleteItem(it.itemId) }
                    itemToDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Expenses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },

                actions = {

                    // Button to View Balances/Settlement
                    IconButton(onClick = {
                        if (viewModel.groupId.isNotEmpty()) {
                            navController.navigate(Screen.GroupExpense.createRoute(viewModel.groupId))
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Assessment, // Or use Icons.Default.AccountBalanceWallet
                            contentDescription = "View Balances"
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.AddMember.createRoute(viewModel.groupId)) }) {
                        Icon(Icons.Default.PersonAdd, "Add Member")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddItem.createRoute(viewModel.groupId)) }) {
                Icon(Icons.Default.Add, "Add", tint = Color.White)
            }
        }
    ) { padding ->
        // 🔥 Pull To Refresh Wrapper
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshData(isManual = true) },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column {
                if (state.summary.isNotEmpty()) { SummaryCard(state.summary) }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
// Inside GroupDetailScreen -> LazyColumn
                        items(items = state.items, key = { it.itemId }) { item ->
                            SwipeToDeleteContainer(
                                item = item,
                                onDeleteRequest = { itemToDelete = item },
                                onClick = {
                                    // 🔥 LINK: Navigate to the Item Detail Screen
                                    navController.navigate(Screen.ItemDetail.createRoute(item.itemId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    item: ItemDto,
    onDeleteRequest: () -> Unit,
    onClick: () -> Unit // 🔥 Add this
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequest()
                false
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled
            val color by animateColorAsState(if (isSwiping) Color.Red.copy(alpha = 0.8f) else Color.Transparent, label = "")

            Box(
                modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.White)
            }
        }
    ) {
        // 🔥 Pass the onClick to the row
        ExpenseItemRow(item = item, onClick = onClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItemRow(
    item: ItemDto,
    onClick: () -> Unit // 🔥 Add this
) {
    Card(
        onClick = onClick, // 🔥 Make the card clickable
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.itemName.take(1).uppercase(), color = Color.White)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.itemName, fontWeight = FontWeight.Bold)
                Text("Paid by ${item.itemPayer.firstOrNull() ?: "User"}", style = MaterialTheme.typography.bodySmall)
            }
            Text("₹${item.itemTotalAmount}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SummaryCard(summary: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Balances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            summary.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}