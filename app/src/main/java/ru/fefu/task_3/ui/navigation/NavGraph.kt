package ru.fefu.task_3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.fefu.task_3.ui.screens.DetailScreen
import ru.fefu.task_3.ui.screens.FavoritesScreen
import ru.fefu.task_3.ui.screens.ListScreen
import ru.fefu.task_3.ui.viewmodel.MainViewModel

@Composable
fun NavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            val state by viewModel.listState.collectAsState()
            val favorites by viewModel.favorites.collectAsState()
            val pageNumber by viewModel.pageNumber.collectAsState()

            ListScreen(
                state = state,
                favorites = favorites,
                pageNumber = pageNumber,
                onQueryChange = viewModel::onSearchQueryChanged,
                onAnimeClick = { id -> navController.navigate("detail/$id") },
                onFavoriteClick = viewModel::toggleFavorite,
                onNextPage = viewModel::nextPage,
                onPrevPage = viewModel::prevPage,
                onRetry = { viewModel.loadAnimes(reset = true) },
                onGoToFavorites = { navController.navigate("favorites") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            
            androidx.compose.runtime.LaunchedEffect(id) {
                viewModel.loadAnimeDetail(id)
            }

            val state by viewModel.detailState.collectAsState()
            val favorites by viewModel.favorites.collectAsState()

            DetailScreen(
                state = state,
                isFavorite = favorites.contains(id),
                onFavoriteClick = { viewModel.toggleFavorite(id) },
                onRetry = { viewModel.loadAnimeDetail(id) },
                onBack = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            val state by viewModel.favoritesListState.collectAsState()
            val favorites by viewModel.favorites.collectAsState()

            FavoritesScreen(
                state = state,
                favorites = favorites,
                onAnimeClick = { id -> navController.navigate("detail/$id") },
                onFavoriteClick = { id -> 
                    viewModel.toggleFavorite(id)
                    viewModel.loadFavorites()
                },
                onBack = { navController.popBackStack() },
                onRefresh = { viewModel.loadFavorites() }
            )
        }
    }
}
