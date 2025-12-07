package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.aman.payplit.model.UserInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController) {

    val userName = remember { mutableStateOf("") }
    val userPhoneNo = remember { mutableStateOf("") }
    val (userEmail, setUserEmail) = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")
    val (isEmailError, setIsEmailError) = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SignUp", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                )
            )
        }
    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Name
            TextField(
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text("Enter Name") },
                modifier = Modifier.size(300.dp, 60.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = colorResource(id = R.color.purple_500),
                    unfocusedContainerColor = colorResource(id = R.color.purple_500),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(Modifier.height(20.dp))

            // Phone
            TextField(
                value = userPhoneNo.value,
                onValueChange = { newValue ->
                    userPhoneNo.value = newValue.filter { it.isDigit() }
                },
                label = { Text("Enter Phone No") },
                modifier = Modifier.size(300.dp, 60.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                shape = RoundedCornerShape(6.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = colorResource(id = R.color.purple_500),
                    unfocusedContainerColor = colorResource(id = R.color.purple_500),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(Modifier.height(20.dp))

            // Email
            TextField(
                value = userEmail,
                onValueChange = { email ->
                    setUserEmail(email)
                    setIsEmailError(!emailRegex.matches(email))
                },
                label = { Text("Enter Email") },
                modifier = Modifier.size(300.dp, 60.dp),
                isError = isEmailError,
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = if (isEmailError) Color.Red else colorResource(id = R.color.purple_500),
                    unfocusedContainerColor = if (isEmailError) Color.Red else colorResource(id = R.color.purple_500),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(Modifier.height(20.dp))

            // Password
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Enter Password") },
                modifier = Modifier.size(300.dp, 60.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                shape = RoundedCornerShape(6.dp),
                visualTransformation = if (passwordVisible.value)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }) {
                        Icon(
                            painter =
                            if (!passwordVisible.value)
                                painterResource(id = R.drawable.ic_visibility)
                            else
                                painterResource(id = R.drawable.ic_visibilityoff),
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = colorResource(id = R.color.purple_500),
                    unfocusedContainerColor = colorResource(id = R.color.purple_500),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(Modifier.height(50.dp))

            // SIGN UP BUTTON
            Button(
                onClick = {
                    if (userName.value.isNotEmpty() &&
                        userPhoneNo.value.isNotEmpty() &&
                        userEmail.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        !isEmailError
                    ) {

                        scope.launch {
                            try {
                                val response = AppGlobalObj.userApiObj.createUser(
                                    UserInfo(
                                        userId = "",
                                        name = userName.value,
                                        mobileNo = userPhoneNo.value,
                                        email = userEmail,
                                        password = password.value,
                                        groupIds = emptyList()
                                    )
                                )

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "User created successfully!", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Enter all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) Color.White else Color.Black,
                    contentColor = if (isDarkTheme) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("SignUp", fontSize = 18.sp)
            }
        }
    }
}
