package com.aman.payplit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aman.payplit.presentation.add_group.AddGroupScreen
import com.aman.payplit.presentation.add_member.AddMemberScreen
import com.aman.payplit.presentation.group.GroupDashboardScreen
import com.aman.payplit.presentation.group_detail.GroupDetailScreen
import com.aman.payplit.presentation.login.LoginPage
import com.aman.payplit.presentation.add_item.AddItemScreen
import com.aman.payplit.presentation.add_item.AddItemDetailScreen
import com.aman.payplit.presentation.item_detail.ItemDetailScreen
import com.aman.payplit.presentation.settlement.SettlementScreen

import com.aman.payplit.presentation.signup.SignUpPage

// Import your screens here (Example: import com.aman.payplit.presentation.screens.LoginPage)

@Composable
fun PayPlitNavigation(navController: NavHostController) {


    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Login Screen
        composable(route = Screen.Login.route) {
             LoginPage(navController)
            // Note: Replace this with your actual screen call
        }

        // 2. SignUp Screen
        composable(route = Screen.SignUp.route) {
             SignUpPage(navController)
        }

        // 3. Group Dashboard (Home)
        composable(route = Screen.GroupDashboard.route) {
            // We will build this next
             GroupDashboardScreen(navController = navController)
        }

        // 🔥 ADD THIS BLOCK
        composable(route = Screen.AddGroup.route) {
            AddGroupScreen(navController = navController)
        }

        composable(
            route = Screen.AddMember.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            AddMemberScreen(navController = navController)
        }


        // Group Details Screen (The Linking Part)
        composable(
            route = Screen.GroupDetails.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) {
            // Note: We don't need to manually pass groupId to the ViewModel
            // because our ViewModel uses SavedStateHandle to get it!
            GroupDetailScreen(navController = navController)
        }

        // 3. Main Groups Dashboard
        composable(route = Screen.Group.route) {
            // GroupPage(navController)
        }

        // 4. Group Items (Passing arguments correctly)
        composable(
            route = Screen.GroupItems.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            // GroupItemsPage(navController, groupId)
        }

        // 5. Add Item to a specific group
        composable(
            route = Screen.AddItem.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 🔥 Extract the groupId from the navigation arguments
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

            // 🔥 Pass it to the screen
            AddItemScreen(navController = navController, groupId = groupId)
        }

// Step 2: Split logic
        composable(
            route = Screen.AddItemDetail.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType }
            )
        ) {
            // We don't pass anything here because AddItemViewModel
            // uses SavedStateHandle to grab these 3 automatically!
            AddItemDetailScreen(navController = navController)
        }

        // 🔥 ADD THIS BLOCK FOR SETTLEMENT
        composable(
            route = Screen.GroupExpense.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) {
            SettlementScreen(navController = navController)
        }

        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) {
            ItemDetailScreen(navController = navController)
        }
    }
}