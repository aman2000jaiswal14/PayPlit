package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.aman.payplit.globalPP.AppGlobalObj.auth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController) {
    val user = auth.currentUser
    if(user != null)
    {
        navController.navigate("GroupPage")
    }
    val userEmail = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val myContext = LocalContext.current
    val passwordVisible = remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
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

                TextField(
                    value = userEmail.value, onValueChange = {
                        userEmail.value = it
                    },
                    label = { Text(text = "Enter email") },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    modifier = Modifier.size(300.dp, 60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = password.value, onValueChange = {
                    password.value = it
                },
                    label = { Text(text = "Enter Password") },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    modifier = Modifier.size(300.dp, 60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(
                                painter = if (passwordVisible.value) painterResource(id = R.drawable.ic_visibilityoff) else painterResource(
                                    id = R.drawable.ic_visibility
                                ), contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = {
                            if(userEmail.value.isNotEmpty() && password.value.isNotEmpty()){
                                auth.signInWithEmailAndPassword(userEmail.value,password.value).addOnCompleteListener {
                                    task ->
                                    if(task.isSuccessful)
                                    {
                                        navController.navigate("GroupPage")
                                    }
                                    else{
                                        Toast.makeText(myContext,"Invalid Email or Password",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(myContext,"Enter Email or Password",Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier =
                        Modifier
                            .width(120.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.White else Color.Black,
                            contentColor = if (isDarkTheme) Color.Black else Color.White,
                        ),
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Text(
                            text = "LogIn",
                            style = MaterialTheme.typography.bodyLarge

                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(
                        onClick = { navController.navigate("SignUpPage") },
                        modifier =
                        Modifier
                            .width(120.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.White else Color.Black,
                            contentColor = if (isDarkTheme) Color.Black else Color.White,
                        ),
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Text(
                            text = "SignUp",
                            style = MaterialTheme.typography.bodyLarge,

                            )
                    }
                }


            }
        }
    )
}