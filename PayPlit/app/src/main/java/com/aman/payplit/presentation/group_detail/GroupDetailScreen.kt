package com.aman.payplit.presentation.group_detail

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.aman.payplit.data.remote.dto.ItemDto
import com.aman.payplit.presentation.group.SharedGroupViewModel
import com.aman.payplit.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    navController: NavController,
    viewModel: SharedGroupViewModel
) {
    val state by viewModel.detailState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Endless Scroll
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLoading && !state.isRefreshing) {
            viewModel.loadNextItems()
        }
    }

    // Lifecycle Refresh
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME)
            {
                // 🔥 This is what catches the new balances after Settle Up
                viewModel.fetchSummaryOnly()
                viewModel.refreshGroupData(isManual = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // User Notifications
    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    // Delete Confirmation
    var itemToDelete by remember { mutableStateOf<ItemDto?>(null) }
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Expense?") },
            text = { Text("Delete '${itemToDelete?.itemName}'?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.let { viewModel.deleteItem(it.itemId) }
                    itemToDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { itemToDelete = null }) { Text("Cancel") } }
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
                    IconButton(onClick = { if (viewModel.groupId.isNotEmpty()) navController.navigate(Screen.GroupExpense.createRoute(viewModel.groupId)) }) {
                        Icon(Icons.Default.Assessment, "Balances")
                    }
                    IconButton(onClick = { if (viewModel.groupId.isNotEmpty()) navController.navigate(Screen.AddMember.createRoute(viewModel.groupId)) }) {
                        Icon(Icons.Default.PersonAdd, "Add Member")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (viewModel.groupId.isNotEmpty()) navController.navigate(Screen.AddItem.createRoute(viewModel.groupId)) }) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshGroupData(isManual = true) },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (state.summary.isNotEmpty()) {
                    SummaryCard(state.summary)
                }

                Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                    // Center Loader (Only if everything is empty)
                    if (state.isLoading && state.items.isEmpty() && state.summary.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = state.items.distinctBy { it.itemId }, key = { it.itemId }) { item ->
                            SwipeToDeleteContainer(
                                item = item,
                                onDeleteRequest = { itemToDelete = item },
                                onClick = { navController.navigate(Screen.ItemDetail.createRoute(item.itemId)) }
                            )
                        }
                        if (state.isPaginationLoading) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(item: ItemDto, onDeleteRequest: () -> Unit, onClick: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequest(); false
            } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled
            val color by animateColorAsState(if (isSwiping) Color.Red.copy(alpha = 0.8f) else Color.Transparent, label = "")
            Box(Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) { Icon(Icons.Default.Delete, "Delete", tint = Color.White) }
        }
    ) { ExpenseItemRow(item, onClick) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItemRow(item: ItemDto, onClick: () -> Unit) {

    val isPayment = item.itemType == "PAYMENT"

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),colors = CardDefaults.cardColors(
        // Use a subtle green background for payments to make them look "Positive"
        containerColor = if (isPayment) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
    )) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), shape = CircleShape, color = if (isPayment) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary) {
                if (isPayment) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(item.itemName.take(1).uppercase(), color = Color.White)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.itemName, fontWeight = FontWeight.Bold, fontSize = 16.sp,color = if (isPayment) Color(0xFF2E7D32) else Color.Unspecified)
                val payer = item.itemPayerNames.firstOrNull() ?: "User"
                val receiver = item.itemSpliterNames.firstOrNull() ?: "Someone"

                Text(
                    text = if (isPayment) "$payer paid $receiver" else "Paid by $payer",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text("₹${item.itemTotalAmount}", fontWeight = FontWeight.ExtraBold, color = if (isPayment) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,fontSize = 16.sp)
        }
    }
}

@Composable
fun SummaryCard(summary: List<String>) {
    Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Your Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            summary.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}