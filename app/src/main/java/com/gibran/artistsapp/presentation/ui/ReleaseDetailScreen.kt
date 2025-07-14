package com.gibran.artistsapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.gibran.artistsapp.R
import com.gibran.artistsapp.domain.model.Track
import com.gibran.artistsapp.presentation.ui.components.LoadingContent
import com.gibran.artistsapp.presentation.ui.components.ErrorCard
import com.gibran.artistsapp.presentation.viewmodel.ReleaseDetailViewModel
import com.gibran.artistsapp.presentation.viewmodel.ReleaseDetailIntent
import com.gibran.artistsapp.presentation.viewmodel.ReleaseDetailEvent
import com.gibran.artistsapp.ui.theme.spacing
import com.gibran.artistsapp.ui.theme.componentSizes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import com.gibran.artistsapp.domain.model.ReleaseDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseDetailScreen(
    releaseId: Long,
    viewModel: ReleaseDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val event by viewModel.event.collectAsState()

    LaunchedEffect(releaseId) {
        viewModel.onIntent(ReleaseDetailIntent.Load(releaseId))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.album_details_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_button))
                }
            }
        )

        when (event) {
            ReleaseDetailEvent.Loading -> {
                LoadingContent(message = stringResource(R.string.loading_album_details))
            }

            is ReleaseDetailEvent.Error -> {
                ErrorCard(
                    message = (event as ReleaseDetailEvent.Error).message,
                    onRetry = { viewModel.onIntent(ReleaseDetailIntent.Retry(releaseId)) }
                )
            }

            is ReleaseDetailEvent.Success -> {
                val successEvent = event as ReleaseDetailEvent.Success
                ReleaseDetailContent(release = successEvent.detail)
            }
        }
    }
}

@Composable
private fun ReleaseDetailContent(
    release: ReleaseDetail
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.md)
    ) {
        item {
            // Album cover
            release.images?.firstOrNull()?.uri?.let { imageUrl ->
                val context = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .memoryCacheKey(imageUrl)
                        .diskCacheKey(imageUrl)
                        .build(),
                    contentDescription = release.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(componentSizes.albumCoverLarge)
                        .clip(RoundedCornerShape(spacing.md)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(spacing.md))
            }

            Text(
                text = release.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            release.year?.let {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (release.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = stringResource(R.string.genres_label, release.genres.joinToString(", ")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (release.styles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(
                    text = stringResource(R.string.styles_label, release.styles.joinToString(", ")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }

        if (release.tracklist.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.tracklist_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(spacing.sm))
            }

            items(release.tracklist) { track ->
                TrackItem(track = track)
            }
        }
    }
}

@Composable
private fun TrackItem(track: Track) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(componentSizes.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${track.position}. ${track.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            track.duration?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
