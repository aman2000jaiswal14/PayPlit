package com.aman.payplit.view

import android.widget.Toast
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aman.payplit.R
import com.aman.payplit.globalPP.AppGlobalObj.addExpenseName
import com.aman.payplit.globalPP.AppGlobalObj.addExpensePrice
import com.aman.payplit.globalPP.AppGlobalObj.currentSelectedGroup
import com.aman.payplit.globalPP.AppGlobalObj.groupApiObj
import com.aman.payplit.globalPP.AppGlobalObj.itemApiObj
import com.aman.payplit.globalPP.AppGlobalObj.userApiObj
import com.aman.payplit.globalPP.AppGlobalObj.currentUserId

import com.aman.payplit.model.GroupItem
import com.aman.payplit.model.UserIdNameVal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDetailPage(navController: NavController) {

    val expanded = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val members = remember { mutableStateOf(listOf<UserIdNameVal>()) }
    val splitValues = remember { mutableStateListOf<String>() }
    val memberSelected = remember { mutableStateListOf<Boolean>() }

    var splitEqually by remember { mutableStateOf(false) }

    fun getDate() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    fun getTime() = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    /** LOAD MEMBERS **/
    LaunchedEffect(Unit) {
        try {
            val res = groupApiObj.getAllGroupMembersDetail(currentSelectedGroup.groupId)
            val mapped = res.map { UserIdNameVal(it.userId, it.name, "0") }

            members.value = mapped
            splitValues.clear()
            splitValues.addAll(List(mapped.size) { "0" })
            memberSelected.clear()
            memberSelected.addAll(List(mapped.size) { true }) // all selected by default

        } catch (e: Exception) {
            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Split equally among selected members **/
    fun updateSplitEqually() {
        val total = addExpensePrice.toDoubleOrNull() ?: 0.0
        val selectedCount = memberSelected.count { it }
        if (selectedCount > 0) {
            val equalSplit = (total / selectedCount).toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)
            splitValues.clear()
            splitValues.addAll(memberSelected.map { if (it) equalSplit.toString() else "0" })
        } else {
            // No member selected, all 0
            splitValues.clear()
            splitValues.addAll(List(memberSelected.size) { "0" })
        }
    }

    /** Reset all to 0 **/
    fun resetSplits() {
        splitValues.clear()
        splitValues.addAll(List(memberSelected.size) { "0" })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item", color = Color.White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple_500)
                ),
                actions = {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
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
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- Display Item Name & Amount ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6C63FF))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Item: $addExpenseName",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Total Amount: ₹$addExpensePrice",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }

            // ---------- Split Equally Checkbox ----------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = splitEqually,
                    onCheckedChange = {
                        splitEqually = it
                        if (splitEqually) updateSplitEqually()
                        else resetSplits()
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF6C63FF),
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Split Equally", fontSize = 18.sp)
            }

            Text(
                "Split Between Members",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            /** MEMBER LIST WITH CHECKBOX **/
            members.value.forEachIndexed { index, user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = memberSelected[index],
                            onCheckedChange = {
                                memberSelected[index] = it
                                if (splitEqually) updateSplitEqually()
                                else if (!it) splitValues[index] = "0"
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF6C63FF),
                                uncheckedColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = user.name,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )

                        TextField(
                            value = splitValues[index],
                            onValueChange = {
                                splitValues[index] = it
                                if (splitEqually) splitEqually = false // uncheck if user edits manually
                            },
                            singleLine = true,
                            modifier = Modifier.width(110.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Color(0xFF6C63FF),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            enabled = memberSelected[index]
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            /** SUBMIT BUTTON **/
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = itemApiObj.createItem(
                                GroupItem(
                                    "1",
                                    addExpenseName,
                                    currentSelectedGroup.groupId,
                                    getDate(),
                                    getTime(),
                                    addExpensePrice,
                                    listOf(currentUserId),
                                    members.value.map { it.userId },
                                    splitValues.toList()
                                )
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(ctx, "Expense Added", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(ctx, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit", fontSize = 20.sp)
            }
        }
    }
}
