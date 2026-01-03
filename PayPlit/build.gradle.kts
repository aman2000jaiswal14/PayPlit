plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false // Fixed name
    alias(libs.plugins.kapt) apply false // Fixed name
    alias(libs.plugins.ksp) apply false // Add this line
}