package com.aman.payplit.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aman.payplit.ui.theme.PayPlitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PayPlitTheme {
                PayPlitNavigation()
            }
        }
    }
}

@Composable
fun PayPlitNavigation(){

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "LoginPage"){
        composable(route = "LoginPage"){
            LoginPage(navController)
        }
        composable(route = "SignUpPage"){
            SignUpPage(navController)
        }
        composable(route = "GroupPage"){
            GroupPage(navController)
        }
        composable(route = "GroupItemsPage"){
            GroupItemsPage(navController)
        }
        composable(route = "ItemDetailPage"){
            ItemDetailPage(navController)
        }
        composable(route = "AddGroupPage"){
            AddGroupPage(navController)
        }
        composable(route = "AddItemPage"){
            AddItemPage(navController)
        }
        composable(route = "AddItemDetailPage"){
            AddItemDetailPage(navController)
        }
        composable(route = "AddMemberInGroup"){
            AddMemberInGroup(navController)
        }

    }

}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PayPlitTheme {
//        Greeting("Android")
//    }
//}