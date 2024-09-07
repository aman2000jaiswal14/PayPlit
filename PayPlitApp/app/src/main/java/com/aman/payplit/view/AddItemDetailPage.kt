package com.aman.payplit.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
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
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.globalPP.AppGlobalObj.itemApiObj
import com.aman.payplit.globalPP.AppGlobalObj.userApiObj
import com.aman.payplit.model.GroupItem
import com.aman.payplit.model.UserGroups
import com.aman.payplit.model.UserIdNameVal
import com.aman.payplit.model.UserInfo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDetailPage(navController: NavController){
    val createStatus  =  remember {
        mutableStateOf("")
    }
    val groupMembers = remember {
        mutableStateOf(emptyList<UserIdNameVal>())
    }


    val itemSpliter = remember {
        mutableStateListOf<String>()
    }
    val itemSpliterValue = remember {
        mutableStateListOf<String>()
    }

    val scope = rememberCoroutineScope()
    val myContext = LocalContext.current
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    LaunchedEffect(key1 = Unit) {
        try {
            val membersData = groupApiObj.getAllGroupMembersDetail(currentSelectedGroup.groupId)

            for(memberData : UserInfo in membersData){
                groupMembers.value += UserIdNameVal(memberData.userId,memberData.name,"0")
                itemSpliter.add(memberData.userId)
            }
            itemSpliterValue.addAll(List(membersData.size) { "0" })

        } catch (e: Exception) {
            println("Item Detail Error fetching: ${e.message}")
        }

    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Item", color = Color.White, fontSize = 20.sp) },
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
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        groupMembers.value.forEachIndexed { index, user ->
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Text(text = user.name, color = Color.White, fontSize = 20.sp,modifier = Modifier.weight(1f))
                                TextField(
                                    value = itemSpliterValue.getOrElse(index) { "" }, onValueChange = { newValue  ->
                                        itemSpliterValue[index] = newValue
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),modifier = Modifier
                                        .width(100.dp)
                                        .padding(start = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        ElevatedButton(onClick = {

                            scope.launch {
                                try {
                                    val responseBody = itemApiObj.createItem(
                                        GroupItem(
                                            "1",
                                            addExpenseName,
                                            currentSelectedGroup.groupId,
                                            getCurrentDate(),
                                            getCurrentTime(),
                                            addExpensePrice,
                                            listOf(auth.currentUser?.uid.toString()),
                                            itemSpliter.toList(),
                                            itemSpliterValue.toList()
                                        )
                                    )
                                    if (responseBody.isSuccessful) {
                                        navController.popBackStack()
                                        navController.popBackStack()
                                        Toast.makeText(
                                            myContext,
                                            "Expense Added",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            myContext,
                                            "Error : ${responseBody.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                } catch (e : Exception){
                                    Toast.makeText(
                                        myContext,
                                        "Error : ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            /*
                            scope.launch {


                                try {
                                    val responseBody = itemApiObj.createGroup(
                                        UserGroups("1",groupName.value,
                                        listOf(auth.currentUser?.uid.toString()), emptyList()
                                    )
                                    )
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
                                Toast.makeText(myContext,createStatus.value, Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
                            */

                        }) {
                            Text(text = "Add", color = Color.White, fontSize = 20.sp)
                        }
                    }

                }


            }
        }
    )
}