package com.gibran.artistsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gibran.artistsapp.presentation.ui.ArtistSearchScreen
import kotlinx.serialization.Serializable

@Serializable
object ArtistSearchDestination


@Serializable
data class ArtistDetailDestination(
    val artistId: Long
)

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ArtistSearchDestination,
        modifier = modifier
    ) {
        composable<ArtistSearchDestination> {
            ArtistSearchScreen(
                onArtistClick = { artistId ->
                    navController.navigate(ArtistDetailDestination(artistId))
                }
            )
        }

        composable<ArtistDetailDestination> { backStackEntry ->
            val artistId =
                backStackEntry.arguments?.getString("artistId")?.toLongOrNull()
            if (artistId != null) {
                // TODO: Implement ArtistDetailScreen
            }
        }
    }
}