package com.gibran.artistsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gibran.artistsapp.presentation.ui.ArtistDetailScreen
import com.gibran.artistsapp.presentation.ui.ArtistSearchScreen
import com.gibran.artistsapp.presentation.ui.DiscographyScreen
import kotlinx.serialization.Serializable

@Serializable
object ArtistSearchDestination


@Serializable
data class ArtistDetailDestination(
    val artistId: Long
)

@Serializable
data class DiscographyDestination(
    val artistId: Long,
    val artistName: String
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
            val args =
                backStackEntry.toRoute<ArtistDetailDestination>()
            ArtistDetailScreen(
                artistId = args.artistId,
                onSeeDiscography = { id, artistName ->
                    navController.navigate(DiscographyDestination(id, artistName))
                }
            )
        }

        composable<DiscographyDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<DiscographyDestination>()
            DiscographyScreen(
                artistId = args.artistId,
                artistName = args.artistName,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}