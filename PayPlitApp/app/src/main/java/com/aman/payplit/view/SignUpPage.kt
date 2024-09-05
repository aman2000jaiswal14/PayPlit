package com.aman.payplit.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController){
    val userName = remember {
        mutableStateOf("")
    }
    val userPhoneNo = remember {
        mutableStateOf("")
    }
    val (userEmail, setUserEmail) = remember {
        mutableStateOf("")
    }
    val password = remember{
        mutableStateOf("")
    }
    val passwordVisible = remember {
        mutableStateOf(false)
    }
    val (isEmailError, setIsEmailError) = remember { mutableStateOf(false) }
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$")
    val isDarkTheme = isSystemInDarkTheme()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "SignUp", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.purple_500)
                )
            )
        },
        content = { paddingValue->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

                TextField(value = userName.value, onValueChange = {userName.value = it},
                    label = { Text(text = "Enter Name")},
                    modifier = Modifier.size(300.dp,60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ))

                Spacer(modifier = Modifier.height(20.dp))

                TextField(value = userPhoneNo.value, onValueChange = { newValue->
                    val filteredValue = newValue.filter{ it.isDigit() }
                    userPhoneNo.value = filteredValue},
                    label = { Text(text = "Enter Phone No")},
                    modifier = Modifier.size(300.dp,60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(value = userEmail, onValueChange = { emailValue ->
                    setUserEmail(emailValue)
                    setIsEmailError(!emailRegex.matches(emailValue))},
                    label = { Text(text = "Enter Email")},
                    modifier = Modifier.size(300.dp,60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    isError = isEmailError,
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500),
                        errorContainerColor = Color.Red
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        if(isEmailError){
                            Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = password.value, onValueChange = {password.value = it},
                    label = { Text(text = "Enter Password")},
                    modifier = Modifier.size(300.dp,60.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    shape = RoundedCornerShape(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = colorResource(id = R.color.purple_500)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if(passwordVisible.value) VisualTransformation.None
                        else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                    Image(painter = if(!passwordVisible.value) painterResource(id = R.drawable.ic_visibility)
                                        else painterResource(id = R.drawable.ic_visibilityoff),
                                        contentDescription ="Toggle password visibility" )
                                }
                            })

                Spacer(modifier = Modifier.height(50.dp))
                Button(

                    onClick = {},

                    modifier =
                        Modifier.wrapContentWidth()
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
        })
}