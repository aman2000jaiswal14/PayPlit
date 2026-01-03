package com.aman.payplit.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aman.payplit.data.local.SessionManager
import com.aman.payplit.presentation.navigation.Screen
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _startDestination = mutableStateOf(Screen.Login.route)
    val startDestination: State<String> = _startDestination

    init {
        // 🔥 INDUSTRY LOGIC: Check if session exists
        val userId = sessionManager.getUserId()
        if (userId != null) {
            _startDestination.value = Screen.GroupDashboard.route
        } else {
            _startDestination.value = Screen.Login.route
        }
    }
}