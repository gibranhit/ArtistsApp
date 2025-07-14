package com.gibran.artistsapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.SortOption
import com.gibran.artistsapp.presentation.ui.components.EmptyState
import com.gibran.artistsapp.presentation.ui.components.ErrorCard
import com.gibran.artistsapp.presentation.ui.components.LoadingContent
import com.gibran.artistsapp.presentation.viewmodel.DiscographyIntent
import com.gibran.artistsapp.presentation.viewmodel.DiscographyViewModel
import com.gibran.artistsapp.ui.theme.spacing
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.R
import androidx.compose.ui.res.stringResource
import com.gibran.artistsapp.presentation.ui.components.LoadingMoreIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscographyScreen(
    artistId: Long,
    artistName: String,
    viewModel: DiscographyViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onReleaseClick: (Long) -> Unit = {}
) {
    val releases = viewModel.releases.collectAsLazyPagingItems()
    val currentFilter by viewModel.currentFilterState.collectAsState()

    LaunchedEffect(artistId) {
        viewModel.onIntent(DiscographyIntent.Load(artistId))
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.discography_title, artistName)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_button))
                }
            }
        )

        DiscographyContent(
            releases = releases,
            currentFilter = currentFilter,
            onFilterApply = { filter ->
                viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, filter))
            },
            onReleaseClick = onReleaseClick
        )
    }
}

@Composable
private fun DiscographyContent(
    releases: LazyPagingItems<Release>,
    currentFilter: DiscographyFilter,
    onFilterApply: (DiscographyFilter) -> Unit,
    onReleaseClick: (Long) -> Unit
) {
    Column {
        SortBar(
            currentFilter = currentFilter,
            onFilterApply = onFilterApply
        )

        when (val refreshState = releases.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContent()
                }
            }
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorCard(
                        message = refreshState.error.localizedMessage
                            ?: stringResource(R.string.error_loading_releases),
                        onRetry = { releases.retry() }
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (releases.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = stringResource(R.string.no_releases_title),
                            subtitle = stringResource(R.string.no_releases_subtitle)
                        )
                    }
                } else {
                    ReleasesContent(releases = releases, onReleaseClick = onReleaseClick)
                }
            }
        }
    }
}

@Composable
private fun ReleasesContent(releases: LazyPagingItems<Release>, onReleaseClick: (Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        items(count = releases.itemCount) { index ->
            val release = releases[index]
            release?.let {
                ReleaseCard(release = it, onReleaseClick = onReleaseClick)
            }
        }

        item {
            when (releases.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingMoreIndicator()
                    }
                }

                is LoadState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorCard(
                            message = stringResource(R.string.error_loading_more_releases),
                            onRetry = { releases.retry() }
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    // No more items to load or not loading
                }
            }
        }
    }
}

@Composable
private fun SortBar(
    currentFilter: DiscographyFilter,
    onFilterApply: (DiscographyFilter) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.sm)
    ) {
        Row(
            modifier = Modifier.padding(componentSizes.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.sort_by, currentFilter.sortBy.displayName),
                style = MaterialTheme.typography.titleMedium
            )

            Box {
                IconButton(onClick = { showSortMenu = !showSortMenu }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, stringResource(R.string.sort_action))
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.entries.forEach { sortOption ->
                        DropdownMenuItem(
                            text = { Text(sortOption.displayName) },
                            onClick = {
                                onFilterApply(currentFilter.copy(sortBy = sortOption))
                                showSortMenu = false
                            },
                            leadingIcon = if (currentFilter.sortBy == sortOption) {
                                {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Sort,
                                        stringResource(R.string.selected)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseCard(release: Release, onReleaseClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReleaseClick(release.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(componentSizes.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album cover
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(release.thumb)
                    .memoryCacheKey(release.thumb)
                    .diskCacheKey(release.thumb)
                    .build(),
                contentDescription = release.title,
                modifier = Modifier
                    .size(componentSizes.artistImageMd)
                    .clip(RoundedCornerShape(spacing.sm)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(spacing.sm))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = release.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(spacing.xs))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    release.year?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it.toString()) }
                        )
                    }

                    AssistChip(
                        onClick = {},
                        label = { Text(release.type) }
                    )
                }

                Text(
                    text = "Role: ${release.role}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReleaseCardPreview() {
    val sampleRelease = Release(
        id = 1,
        title = "Nevermind",
        year = 1991,
        type = "Album",
        role = "Main",
        thumb = "https://placehold.co/200"
    )
    ReleaseCard(release = sampleRelease, onReleaseClick = {})
}
