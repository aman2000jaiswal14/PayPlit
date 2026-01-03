package com.aman.payplit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.aman.payplit.presentation.add_group.AddGroupScreen
import com.aman.payplit.presentation.add_member.AddMemberScreen
import com.aman.payplit.presentation.group.GroupDashboardScreen
import com.aman.payplit.presentation.group.SharedGroupViewModel
import com.aman.payplit.presentation.group_detail.GroupDetailScreen
import com.aman.payplit.presentation.login.LoginPage
import com.aman.payplit.presentation.add_item.AddItemScreen
import com.aman.payplit.presentation.add_item.AddItemDetailScreen
import com.aman.payplit.presentation.item_detail.ItemDetailScreen
import com.aman.payplit.presentation.settlement.SettleUpScreen
import com.aman.payplit.presentation.settlement.SettlementScreen
import com.aman.payplit.presentation.signup.SignUpPage

@Composable
fun PayPlitNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) { LoginPage(navController) }
        composable(route = Screen.SignUp.route) { SignUpPage(navController) }
        composable(route = Screen.GroupDashboard.route) { GroupDashboardScreen(navController) }
        composable(route = Screen.AddGroup.route) { AddGroupScreen(navController) }

        // --- 🔥 NESTED GROUP GRAPH ---
        navigation(
            route = "group_session/{groupId}",
            startDestination = Screen.GroupDetails.route
        ) {
            composable(
                route = Screen.GroupDetails.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                GroupDetailScreen(navController, sharedVm)
            }

            composable(
                route = Screen.AddMember.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                AddMemberScreen(navController, sharedVm)
            }

            composable(
                route = Screen.AddItem.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                AddItemScreen(navController, groupId, sharedVm)
            }

            composable(
                route = Screen.AddItemDetail.route,
                arguments = listOf(
                    navArgument("groupId") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType },
                    navArgument("price") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                AddItemDetailScreen(navController, sharedVm)
            }

            composable(
                route = Screen.GroupExpense.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                SettlementScreen(navController, sharedVm)
            }

            // Moved ItemDetail INSIDE the group session to keep context
            composable(
                route = Screen.ItemDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) {
                ItemDetailScreen(navController)
            }

            composable(
                route = Screen.SettleUp.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("group_session/{groupId}")
                }
                val sharedVm: SharedGroupViewModel = hiltViewModel(parentEntry)
                SettleUpScreen(navController, sharedVm)
            }
        }
    }
}