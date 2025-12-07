package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj
import com.aman.payplit.model.UserGroups
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupPage(navController: NavController) {
    val groupName = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Group", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "More Options")
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "LogOut") },
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
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .height(280.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Group Name",
                            color = Color.White,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(
                            value = groupName.value,
                            onValueChange = { groupName.value = it },
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = Color.White // Text color here
                            ),
                            placeholder = { Text(text = "Enter group name", color = Color.LightGray) },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.White
                                // Do NOT use textColor here
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )


                        Spacer(modifier = Modifier.height(30.dp))

                        ElevatedButton(
                            onClick = {
                                if (groupName.value.isBlank()) {
                                    Toast.makeText(context, "Group name cannot be empty", Toast.LENGTH_SHORT).show()
                                    return@ElevatedButton
                                }

                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val currentUserId = AppGlobalObj.currentUserId
                                        if (currentUserId.isEmpty()) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "User not logged in!", Toast.LENGTH_LONG).show()
                                            }
                                            return@launch
                                        }

                                        val newGroup = UserGroups(
                                            groupId = "",
                                            groupName = groupName.value,
                                            groupMembers = listOf(currentUserId),
                                            groupItems = emptyList()
                                        )

                                        val response = groupApiObj.createGroup(newGroup)

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Group created successfully!", Toast.LENGTH_LONG).show()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Add", fontSize = 20.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}
