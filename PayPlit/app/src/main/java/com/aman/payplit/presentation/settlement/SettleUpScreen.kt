package com.aman.payplit.presentation.settlement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    navController: NavController,
    sharedVm: SharedGroupViewModel
) {
    // 1. Observe state from the Shared ViewModel
    val state by sharedVm.detailState.collectAsState()
    val members by sharedVm.members.collectAsState()

    // 2. Filter logic: Only get people I owe money to
    val creditors = remember(state.groupGraph, members) {
        sharedVm.getCreditors()
    }

    // 3. Local UI state
    var selectedMemberId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    // 4. 🔥 Robust Navigation Logic
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Navigate back to Group Details
            navController.popBackStack()
            // Reset the success state immediately so it's ready for the next action
            sharedVm.resetSuccessState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Cash Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select a friend to pay back:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            // --- SECTION 1: MEMBER SELECTOR ---
            Box(modifier = Modifier.padding(vertical = 16.dp)) {
                val selectedPair = creditors.find { it.first.userId == selectedMemberId }

                OutlinedButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = selectedPair?.first?.name ?: "Tap to choose a person",
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    if (creditors.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("You don't owe anyone!") },
                            onClick = { showMenu = false }
                        )
                    }
                    creditors.forEach { (user, owedAmount) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(user.name ?: "Unknown")
                                    Text("₹$owedAmount", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                }
                            },
                            onClick = {
                                selectedMemberId = user.userId
                                // 🔥 AUTO-FILL: Set the exact amount from the graph
                                amount = owedAmount.toString()
                                showMenu = false
                            }
                        )
                    }
                }
            }

            // --- SECTION 2: THE AMOUNT (LOCKED) ---
            OutlinedTextField(
                value = amount,
                onValueChange = { /* Locked: User cannot edit */ },
                label = { Text("Settlement Amount") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // 🔥 INDUSTRY STANDARD: Prevent partial settlement errors
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                supportingText = {
                    if (selectedMemberId.isNotEmpty()) {
                        Text("This will clear your entire debt with this person.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- SECTION 3: CONFIRM BUTTON ---
            Button(
                onClick = {
                    if (selectedMemberId.isNotEmpty() && amount.isNotEmpty()) {
                        sharedVm.submitPayment(selectedMemberId, amount) {
                            // pop handled by LaunchedEffect
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedMemberId.isNotEmpty() && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50) // Professional Green
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Confirm Cash Payment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Error Display
            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}