package com.aman.payplit.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object SignUp : Screen("signup_screen")
    object Group : Screen("group_screen")
    object AddGroup : Screen("add_group_screen")
    object GroupDashboard : Screen("group_dashboard_screen")



    // Define the route with a placeholder {groupId}
    object GroupDetails : Screen("group_details_screen/{groupId}") {
        // Helper function to build the actual navigation string
        fun createRoute(groupId: String) = "group_details_screen/$groupId"
    }

    // Example of passing arguments: GroupItems needs a groupId
    object GroupItems : Screen("group_items_screen/{groupId}") {
        fun createRoute(groupId: String) = "group_items_screen/$groupId"
    }



    object AddItem : Screen("add_item_screen/{groupId}") {
        fun createRoute(groupId: String) = "add_item_screen/$groupId"
    }

    object AddItemDetail : Screen("add_item_detail_screen/{groupId}/{name}/{price}") {
        fun createRoute(groupId: String, name: String, price: String) =
            "add_item_detail_screen/$groupId/$name/$price"
    }

    object AddMember : Screen("add_member_screen/{groupId}") {
        fun createRoute(groupId: String) = "add_member_screen/$groupId"
    }

    // Add this line for the Settlement screen
    object GroupExpense : Screen("group_expense_screen/{groupId}") {
        fun createRoute(groupId: String) = "group_expense_screen/$groupId"
    }

    object ItemDetail : Screen("item_detail_screen/{itemId}") {
        fun createRoute(itemId: String) = "item_detail_screen/$itemId"
    }
}