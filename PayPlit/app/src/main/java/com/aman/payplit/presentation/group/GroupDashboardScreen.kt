package com.aman.payplit.presentation.group

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aman.payplit.data.remote.dto.GroupDto
import com.aman.payplit.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDashboardScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var groupToDelete by remember { mutableStateOf<GroupDto?>(null) }

    // --- DELETE CONFIRMATION DIALOG ---
    if (groupToDelete != null) {
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Delete Group?") },
            text = { Text("Are you sure you want to delete '${groupToDelete?.groupName}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        groupToDelete?.let { viewModel.deleteGroup(it.groupId) }
                        groupToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { groupToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PayPlit Groups", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadGroups() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                viewModel.logout()
                                navController.navigate(Screen.Login.route) { popUpTo(0) }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddGroup.route) }) {
                Icon(Icons.Default.Add, "Add Group")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.loadGroups(isPullToRefresh = true) },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.groups,
                    key = { it.groupId }
                ) { group ->
                    SwipeableGroupCard(
                        group = group,
                        onDeleteRequest = { groupToDelete = group },
                        onClick = { navController.navigate(Screen.GroupDetails.createRoute(group.groupId)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableGroupCard(
    group: GroupDto,
    onDeleteRequest: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDeleteRequest()
                false // Don't dismiss until confirmed
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled
            val color by animateColorAsState(if (isSwiping) Color.Red.copy(alpha = 0.8f) else Color.Transparent)

            Box(
                Modifier.fillMaxSize().background(color, RoundedCornerShape(16.dp)).padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.White)
            }
        }
    ) {
        GroupCard(group = group, onClick = onClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(group: GroupDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Group, null, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(group.groupName ?: "Unnamed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${group.groupMembers?.size ?: 0} members", style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.KeyboardArrowRight, null)
        }
    }
}