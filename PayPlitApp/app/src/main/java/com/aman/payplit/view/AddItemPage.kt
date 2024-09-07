package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj.addExpenseName
import com.aman.payplit.globalPP.AppGlobalObj.addExpensePrice
import com.aman.payplit.globalPP.AppGlobalObj.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemPage(navController: NavController){
    val itemName = remember {
        mutableStateOf("")
    }
    val itemPrice = remember {
        mutableStateOf("")
    }
    val expanded = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val myContext = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Item", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(
                        id = R.color.purple_500
                    )
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
                                auth.signOut()
                                navController.navigate("LoginPage"){
                                    popUpTo("LoginPage"){inclusive = true}
                                }

                            },

                            )
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(7.dp),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Expense Detail", color = Color.White, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = itemName.value, onValueChange = { it ->
                                itemName.value = it
                            },
                            label = { Text(text = "Expanse Name")},
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = itemPrice.value, onValueChange = { it ->
                                itemPrice.value = it
                            },
                            label = { Text(text = "Expense Price")},
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(onClick = {
                            if(itemName.value.isNotEmpty() && itemPrice.value.isNotEmpty())
                            {
                                addExpenseName = itemName.value
                                addExpensePrice = itemPrice.value
                                navController.navigate("AddItemDetailPage")
                            }
                            else{
                                Toast.makeText(myContext,"Enter Name or Price",Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(text = "Add", color = Color.White, fontSize = 20.sp)
                        }
                    }

                }


            }
        }
    )
}