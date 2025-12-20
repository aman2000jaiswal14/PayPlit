package com.aman.payplit.presentation.add_item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aman.payplit.presentation.navigation.Screen
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDetailScreen(
    navController: NavController,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isEqually by remember { mutableStateOf(false) }

    // 🔥 INDUSTRY GRADE: Real-time Math Validation
    val totalTarget = viewModel.totalAmount.toDoubleOrNull() ?: 0.0
    val currentSum = viewModel.splitValues.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val difference = totalTarget - currentSum
    val isMathCorrect = abs(difference) < 0.01 // Handling small rounding errors

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.popBackStack(route = Screen.GroupDetails.route, inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
            // 1. Summary & Validation Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMathCorrect)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Amount: ₹${viewModel.totalAmount}", fontWeight = FontWeight.Bold)

                    // Show remaining balance logic
                    if (!isMathCorrect) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (difference > 0) "₹${String.format("%.2f", difference)} remaining"
                                else "Over by ₹${String.format("%.2f", abs(difference))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text("✅ Splitting perfectly", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 2. Split Equally Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Checkbox(checked = isEqually, onCheckedChange = {
                    isEqually = it
                    viewModel.toggleSplitEqually(it)
                })
                Text("Split Equally among selected")
            }

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            // 3. Member List
            state.members.forEachIndexed { index, member ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.memberSelected[index],
                        onCheckedChange = { isChecked ->
                            // 🔥 Call the new robust logic
                            viewModel.onMemberCheckedChange(
                                index = index,
                                isSelected = isChecked,
                                isEquallyMode = isEqually
                            )
                        }
                    )
                    Text(member.name ?: "User", modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = viewModel.splitValues[index],
                        onValueChange = { newValue ->
                            // Only allow editing if the user is selected
                            if (viewModel.memberSelected[index]) {
                                viewModel.splitValues[index] = newValue
                                if (isEqually) isEqually = false
                            }
                        },
                        modifier = Modifier.width(110.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = viewModel.memberSelected[index], // 🔥 UI safety: disable field if unchecked
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. SUBMIT BUTTON (Disabled if math is wrong)
            Button(
                onClick = { viewModel.submitExpense() },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(55.dp),
                // 🔥 Block submission if math is wrong OR currently loading
                enabled = !state.isLoading && isMathCorrect
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isMathCorrect) "Submit Expense" else "Amount Mismatch")
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
        }
    }
}