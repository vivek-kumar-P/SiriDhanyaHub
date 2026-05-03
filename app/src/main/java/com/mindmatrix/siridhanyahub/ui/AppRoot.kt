package com.mindmatrix.siridhanyahub.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.mindmatrix.siridhanyahub.navigation.SiriDhanyaNavHost
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    var language by rememberSaveable { mutableStateOf(AppLanguage.ENGLISH) }
    SiriDhanyaNavHost(
        navController = navController,
        language = language,
        onLanguageChange = { language = it }
    )
}
