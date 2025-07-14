package com.gibran.artistsapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.gibran.artistsapp.presentation.ui.components.LoadingContent
import com.gibran.artistsapp.presentation.ui.components.ErrorCard
import com.gibran.artistsapp.presentation.ui.components.ArtistCarousel
import com.gibran.artistsapp.presentation.ui.components.ExpandableCard
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.model.Image
import com.gibran.artistsapp.domain.model.Member
import com.gibran.artistsapp.presentation.viewmodel.ArtistDetailEvent
import com.gibran.artistsapp.presentation.viewmodel.ArtistDetailIntent
import com.gibran.artistsapp.presentation.viewmodel.ArtistDetailViewModel
import com.gibran.artistsapp.ui.theme.spacing
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.R
import androidx.compose.ui.res.stringResource

@Composable
fun ArtistDetailScreen(
    artistId: Long,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    onSeeDiscography: (Long, String) -> Unit
) {
    val event by viewModel.event.collectAsState()

    LaunchedEffect(artistId) {
        viewModel.onIntent(ArtistDetailIntent.Load(artistId))
    }

    when (event) {
        ArtistDetailEvent.Loading -> LoadingContent()
        is ArtistDetailEvent.Error -> ErrorCard(
            message = (event as ArtistDetailEvent.Error).message,
            onRetry = { viewModel.onIntent(ArtistDetailIntent.Retry(artistId)) }
        )
        is ArtistDetailEvent.Success -> ArtistDetailContent(
            (event as ArtistDetailEvent.Success).detail,
            onSeeDiscography
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArtistDetailContent(
    artist: ArtistDetail,
    onSeeDiscography: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.md)
    ) {
        item {
            ArtistCarousel(
                images = artist.images ?: emptyList(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacing.md))
        }

        item {
            Text(text = artist.name, style = MaterialTheme.typography.headlineLarge)
            if (!artist.members.isNullOrEmpty()) {
                Spacer(Modifier.height(spacing.xs))
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.artist_type_band)) },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
            Spacer(Modifier.height(spacing.md))
        }

        if (!artist.profile.isNullOrBlank()) {
            item {
                ExpandableCard(stringResource(R.string.biography_title)) {
                    Text(
                        text = artist.profile,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(spacing.sm))
            }
        }

        if (!artist.members.isNullOrEmpty()) {
            item {
                ExpandableCard(stringResource(R.string.members_title)) {
                    artist.members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(componentSizes.artistImageSm)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.name.first().toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.width(spacing.sm))
                            Column {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(spacing.sm))
            }
        }

        // FAB for discography
        item {
            Spacer(Modifier.height(spacing.lg))
            FloatingActionButton(
                onClick = { onSeeDiscography(artist.id, artist.name) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.see_discography))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtistDetailContentPreview() {
    val sample = ArtistDetail(
        id = 1L,
        name = "Radiohead",
        profile = "Radiohead es una banda de rock alternativo británica…",
        images = listOf(Image("https://placehold.co/400")),
        members = listOf(
            Member(1, "Thom Yorke"),
            Member(2, "Jonny Greenwood"),
            Member(3, "Ed O'Brien"),
            Member(4, "Colin Greenwood"),
            Member(5, "Philip Selway")
        )
    )
    ArtistDetailContent(sample, onSeeDiscography = { _, _ -> })
}
