package com.example.lovelycats_android_sample_app.View

import BreedModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lovelycats_android_sample_app.Managers.PreferencesManager
import com.squareup.moshi.Moshi

@Composable
fun RootView() {
    val context = LocalContext.current

    val preferencesManager = remember { PreferencesManager(context) }
    val hasSeenOnboarding = remember { mutableStateOf(preferencesManager.get(key = "hasSeenOnboarding",
        false)) }


    val navigationController = rememberNavController()

    var startDestination = if (hasSeenOnboarding.value) "cat-listing" else "onboarding-view"

    NavHost(navController = navigationController, startDestination = startDestination) {

        composable(route = "cat-listing") {
            CatListViewRendering(navigationCallBack = {
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(BreedModel::class.java).lenient()
                val modelJson = jsonAdapter.toJson(it)
                navigationController.navigate("cat-detail={model}".replace("{model}", modelJson))
            })
        }

        composable(route = "login-view") {
            LoginView(navController = navigationController)
        }

        composable(route = "onboarding-view") {
            ShowOnboardingView(navController = navigationController)
            preferencesManager.save("hasSeenOnboarding", true)
        }

        composable(route = "scaffold-top") { ScaffoldWithTopBar(navController = navigationController) }

        composable(route = "cat-detail={model}"
//            ,
//            arguments = listOf(navArgument(name = "cat-id") {
//                type = NavType.StringType
//            })
        ) {
            val modelJson =  it.arguments?.getString("model")
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(BreedModel::class.java).lenient()
            val model = jsonAdapter.fromJson(modelJson)
            //CatDetailView(model = model!!)
            CatDetailViewConstraintLayout(model = model!!)
        }
    }
}