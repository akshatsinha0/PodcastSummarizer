package com.example.aipodcast.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.aipodcast.features.details.DetailsScreen
import com.example.aipodcast.features.details.DetailsViewModel
import com.example.aipodcast.features.home.HomeScreen
import com.example.aipodcast.features.home.HomeViewModel
import com.example.aipodcast.features.player.PlayerScreen
import com.example.aipodcast.features.player.PlayerViewModel
import com.example.aipodcast.features.processing.ProcessingScreen
import com.example.aipodcast.App

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        composable<Home> {
            val app = (LocalContext.current.applicationContext as App)
            val viewModel: HomeViewModel = viewModel { 
                HomeViewModel(app.appContainer.getEpisodes, app.appContainer.createEpisodeFromLocal, app.appContainer.createEpisodeFromUrl)
            }
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetails = { episodeId ->
                    navController.navigate(Details(episodeId))
                },
                onNavigateToProcessing = {
                    navController.navigate(Processing)
                }
            )
        }
        
        composable<Details> { backStackEntry ->
            val details: Details = backStackEntry.toRoute()
            val app = (LocalContext.current.applicationContext as App)
            val viewModel: DetailsViewModel = viewModel { 
                DetailsViewModel(app.appContainer.getEpisodeDetail, app.appContainer.exportChapters)
            }
            DetailsScreen(
                episodeId = details.episodeId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPlayer = { episodeId ->
                    navController.navigate(Player(episodeId))
                }
            )
        }
        
        composable<Player> { backStackEntry ->
            val player: Player = backStackEntry.toRoute()
            val app = (LocalContext.current.applicationContext as App)
            val viewModel: PlayerViewModel = viewModel { 
                PlayerViewModel(app.appContainer.getEpisodeDetail, app.appContainer.playerController)
            }
            PlayerScreen(
                episodeId = player.episodeId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Processing> {
            ProcessingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}