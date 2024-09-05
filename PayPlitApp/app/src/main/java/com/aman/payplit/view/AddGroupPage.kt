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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.api.UserGroupsApi
import com.aman.payplit.model.UserGroups
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import retrofit2.HttpException



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupPage(navController: NavController) {
    val baseUrlUserGroups = "http://192.168.29.141:7000"
    val retrofitUserGroup = Retrofit.Builder().baseUrl(baseUrlUserGroups)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val userGroupApi = retrofitUserGroup.create(UserGroupsApi::class.java)
    var createStatus  =  remember {
        mutableStateOf("")
    }
    val groupName = remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val myContext = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Group", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(
                        id = R.color.purple_500
                    )
                )
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
                        .width(300.dp)  // Adjust width as needed
                        .height(250.dp)
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
                        Text(text = "Group Name", color = Color.White, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = groupName.value, onValueChange = { it ->
                                groupName.value = it
                            },
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
                        ElevatedButton(onClick = {
                            scope.launch {
                                try {
                                    val responseBody = userGroupApi.createGroup(UserGroups("1",groupName.value,
                                        listOf("1"), emptyList()
                                    ))
                                    if(responseBody.isSuccessful)
                                    {
                                        createStatus.value = responseBody.body()?.string()?:"No response"
                                    }
                                    else{
                                        createStatus.value = "Error : ${responseBody.message()}"
                                    }
                                }
                                catch (e : Exception){
                                    createStatus.value = "HttpException: ${e.message}"
                                }
                                Toast.makeText(myContext,createStatus.value,Toast.LENGTH_LONG).show()
                                navController.popBackStack()
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