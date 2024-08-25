package com.aman.payplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aman.payplit.ui.theme.PayPlitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(2000L)
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
    }
}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PayPlitTheme {
//        Greeting("Android")
//    }
//}