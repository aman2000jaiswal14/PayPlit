package com.aman.payplit.presentation.add_member

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.payplit.presentation.group.SharedGroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    navController: NavController,
    viewModel: SharedGroupViewModel
) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.detailState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(80.dp))

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Friend's Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.addMember(
                            email = email,
                            onShowMessage = { msg: String ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onSuccess = {
                                Toast.makeText(context, "Member Added", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    // 🔥 INDUSTRY FIX: Added color contrast so it's visible on the button
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // White spinner on Primary Button
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add to Group", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}