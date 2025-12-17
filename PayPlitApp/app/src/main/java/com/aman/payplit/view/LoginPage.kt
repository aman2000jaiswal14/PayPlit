package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj
import com.aman.payplit.globalPP.AppGlobalObj.userApiObj
import com.aman.payplit.model.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController) {
    val userEmail = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                )
            )
        },
        content = { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email
                TextField(
                    value = userEmail.value,
                    onValueChange = { userEmail.value = it },
                    label = { Text("Enter Email") },
                    modifier = Modifier.size(300.dp, 60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Enter Password") },
                    modifier = Modifier.size(300.dp, 60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(
                                painter = if (passwordVisible.value) painterResource(id = R.drawable.ic_visibilityoff) else painterResource(id = R.drawable.ic_visibility),
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row {
                    Button(
                        onClick = {
                            if (userEmail.value.isNotEmpty() && password.value.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val response = userApiObj.loginUser(
                                            mapOf("email" to userEmail.value, "password" to password.value)
                                        )

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                val loginResponse = response.body()
                                                val userId = loginResponse?.data?.userId ?: ""

                                                if (userId.isNotEmpty()) {
                                                    AppGlobalObj.currentUserId = userId
//                                                    val sharedPref = context.getSharedPreferences("PayplitPrefs", Context.MODE_PRIVATE)
//                                                    sharedPref.edit().putString("currentUserId", userId).apply()
                                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("GroupPage") {
                                                        popUpTo("LoginPage") { inclusive = true }
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Login failed: No userId returned", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Enter Email and Password", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.width(120.dp).height(60.dp)
                    ) {
                        Text("LogIn")
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = { navController.navigate("SignUpPage") },
                        modifier = Modifier.width(120.dp).height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.White else Color.Black,
                            contentColor = if (isDarkTheme) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text("SignUp", style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(
                        onClick = {

                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val response = userApiObj.testUser()


                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                val testResponse = response.body()
                                                Toast.makeText(context, "test success: ${response.message()}", Toast.LENGTH_SHORT).show()

                                            } else {
                                                Toast.makeText(context, "test failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                        },
                        modifier = Modifier.width(120.dp).height(60.dp)
                    ) {
                        Text("test")
                    }

                }
            }
        }
    )
}
