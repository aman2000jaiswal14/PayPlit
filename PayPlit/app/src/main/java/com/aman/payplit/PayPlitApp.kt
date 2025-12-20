package com.aman.payplit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 🔥 This is the "Brain" annotation Hilt is looking for
class PayPlitApp : Application()