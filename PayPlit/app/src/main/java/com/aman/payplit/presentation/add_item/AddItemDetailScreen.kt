package com.aman.payplit.presentation.add_item

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.presentation.group.SharedGroupViewModel
import com.aman.payplit.presentation.navigation.Screen
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDetailScreen(
    navController: NavController,
    viewModel: SharedGroupViewModel
) {
    val state by viewModel.detailState.collectAsState()
    val members by viewModel.members.collectAsState()

    var isEqually by remember { mutableStateOf(false) }
    var showPayerMenu by remember { mutableStateOf(false) }

    // Math Validation
    val totalTarget = viewModel.totalAmount.toDoubleOrNull() ?: 0.0
    val currentSum = viewModel.splitValues.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val difference = totalTarget - currentSum
    val isMathCorrect = abs(difference) < 0.01

    // 🔥 INDUSTRY FIX: Robust Navigation
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // 🔥 INDUSTRY FIX:
            // This command tells the NavController to remove all screens
            // until it finds GroupDetails. This clears Step 2 AND Step 1.
            navController.popBackStack(
                route = Screen.GroupDetails.route,
                inclusive = false
            )

            // Clear the success flag so the user can add another item later
            viewModel.resetSuccessState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Details", fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Validation Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMathCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = viewModel.expenseName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Total Amount: ₹${viewModel.totalAmount}", style = MaterialTheme.typography.titleMedium)
                    if (!isMathCorrect) {
                        Text(text = "₹${String.format("%.2f", abs(difference))} mismatch", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 2. Who Paid
            Text("Who paid?", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.padding(16.dp)) {
                val selectedPayer = members.find { it.userId == state.selectedPayerId }
                OutlinedButton(onClick = { showPayerMenu = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedPayer?.name ?: "Select Payer")
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(expanded = showPayerMenu, onDismissRequest = { showPayerMenu = false }) {
                    members.forEach { member ->
                        DropdownMenuItem(text = { Text(member.name ?: "User") }, onClick = {
                            viewModel.onPayerSelected(member.userId)
                            showPayerMenu = false
                        })
                    }
                }
            }

            // 3. Equally Option
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Checkbox(checked = isEqually, onCheckedChange = {
                    isEqually = it
                    viewModel.toggleSplitEqually(it, viewModel.totalAmount)
                })
                Text("Split Equally")
            }

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            // 4. Split List
            members.forEachIndexed { index, member ->
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = viewModel.memberSelected[index], onCheckedChange = {
                        viewModel.onMemberCheckedChange(index, it, isEqually, viewModel.totalAmount)
                    })
                    Text(member.name ?: "User", modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = viewModel.splitValues[index],
                        onValueChange = { viewModel.splitValues[index] = it; if(isEqually) isEqually = false },
                        modifier = Modifier.width(110.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = viewModel.memberSelected[index],
                        singleLine = true
                    )
                }
            }

            Button(
                onClick = { viewModel.submitExpense(viewModel.expenseName, viewModel.totalAmount) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(55.dp),
                enabled = !state.isLoading && isMathCorrect
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Save Expense")
            }
        }
    }
}