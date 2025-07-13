package com.gibran.artistsapp.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.gibran.artistsapp.R
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.presentation.ui.components.ArtistImage
import com.gibran.artistsapp.presentation.ui.components.EmptyState
import com.gibran.artistsapp.presentation.ui.components.ErrorCard
import com.gibran.artistsapp.presentation.ui.components.LoadingContent
import com.gibran.artistsapp.presentation.ui.components.LoadingMoreIndicator
import com.gibran.artistsapp.presentation.ui.components.SubtitleText
import com.gibran.artistsapp.presentation.ui.components.TitleText
import com.gibran.artistsapp.presentation.viewmodel.ArtistSearchIntent
import com.gibran.artistsapp.presentation.viewmodel.ArtistSearchPagingViewModel
import com.gibran.artistsapp.ui.theme.ArtistsAppTheme
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.ui.theme.spacing
import kotlinx.coroutines.flow.retry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSearchScreen(
    onArtistClick: (Long) -> Unit,
    viewModel: ArtistSearchPagingViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchEmpty by viewModel.isSearchEmpty.collectAsState()
    val artists = viewModel.artistsPagingFlow.collectAsLazyPagingItems()

    ArtistSearchContent(
        searchQuery = searchQuery,
        isSearchEmpty = isSearchEmpty,
        artists = artists,
        onArtistClick = onArtistClick,
        onQueryChange = { query -> viewModel.onIntent(ArtistSearchIntent.Search(query)) },
        onClearClick = { viewModel.onIntent(ArtistSearchIntent.ClearSearch) },
        onRetry = { artists.retry() },
    )
}


@Composable
fun ArtistSearchContent(
    searchQuery: String,
    isSearchEmpty: Boolean,
    artists: LazyPagingItems<Artist>,
    onArtistClick: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.md)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onClearClick = onClearClick,
        )

        Spacer(modifier = Modifier.height(spacing.md))

        when {
            isSearchEmpty -> {
                EmptyState(
                    icon = Icons.Default.Search,
                    title = stringResource(R.string.empty_state_title),
                    subtitle = stringResource(R.string.empty_state_subtitle)
                )
            }

            else -> {
                ArtistPagingList(
                    artists = artists,
                    onArtistClick = onArtistClick,
                    onRetry = onRetry
                )
            }
        }
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.search_label)) },
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon_description)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search)
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        singleLine = true,
    )
}

@Composable
private fun ArtistPagingList(
    artists: LazyPagingItems<Artist>,
    onArtistClick: (Long) -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        items(
            count = artists.itemCount,
            key = artists.itemKey { it.id }
        ) { index ->
            val artist = artists[index]
            if (artist != null) {
                ArtistItem(
                    artist = artist,
                    onClick = { onArtistClick(artist.id) }
                )
            }
        }
        when (val refreshState = artists.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingMoreIndicator()
                }
            }

            is LoadState.Error -> {
                item {
                    ErrorCard(
                        message = refreshState.error.localizedMessage
                            ?: stringResource(R.string.error_load_more),
                        onRetry = { artists.retry() }
                    )
                }
            }

            is LoadState.NotLoading -> {
                // No more items to load or not loading
            }
        }
    }

    when (artists.loadState.refresh) {
        is LoadState.Loading -> {
            if (artists.itemCount == 0) {
                LoadingContent(message = stringResource(R.string.loading_message))
            }
        }

        is LoadState.Error -> {
            if (artists.itemCount == 0) {
                ErrorCard(
                    message = (artists.loadState.refresh as LoadState.Error).error.message
                        ?: stringResource(R.string.error_generic),
                    onRetry = onRetry
                )
            }
        }

        is LoadState.NotLoading -> {
            if (artists.itemCount == 0) {
                EmptyState(
                    title = stringResource(R.string.no_results_title),
                    subtitle = stringResource(R.string.no_results_subtitle)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistItem(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(componentSizes.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtistImage(
                imageUrl = artist.imageUrl,
                contentDescription = artist.name,
            )

            Spacer(modifier = Modifier.width(spacing.sm))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TitleText(
                    text = artist.name,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(spacing.xs))
                SubtitleText(
                    text = artist.type.replaceFirstChar { it.uppercase() }
                )
            }
        }
    }
}

// MARK: - Preview Parameter Providers

class ArtistPreviewParameterProvider : PreviewParameterProvider<Artist> {
    override val values = sequenceOf(
        Artist(
            id = 1,
            name = "Radiohead",
            type = "group",
            imageUrl = "https://img.discogs.com/radiohead.jpg",
            resourceUrl = "https://api.discogs.com/artists/1"
        ),
        Artist(
            id = 2,
            name = "Thom Yorke",
            type = "person",
            imageUrl = "https://img.discogs.com/thom.jpg",
            resourceUrl = "https://api.discogs.com/artists/2"
        ),
        Artist(
            id = 3,
            name = "The Beatles",
            type = "group",
            imageUrl = null,
            resourceUrl = "https://api.discogs.com/artists/3"
        )
    )
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun ArtistSearchContentEmptyPreview() {
    ArtistsAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.md)
        ) {
            SearchBar(
                query = "",
                onQueryChange = { },
                onClearClick = { },
            )

            Spacer(modifier = Modifier.height(spacing.md))

            EmptyState(
                icon = Icons.Default.Search,
                title = stringResource(R.string.empty_state_title),
                subtitle = stringResource(R.string.empty_state_subtitle)
            )
        }
    }
}

@Preview(name = "Search Bar Empty", showBackground = true)
@Composable
private fun SearchBarEmptyPreview() {
    ArtistsAppTheme {
        SearchBar(
            query = "",
            onQueryChange = { },
            onClearClick = { },
        )
    }
}

@Preview(name = "Search Bar With Query", showBackground = true)
@Composable
private fun SearchBarWithQueryPreview() {
    ArtistsAppTheme {
        SearchBar(
            query = "Radiohead",
            onQueryChange = { },
            onClearClick = { },
        )
    }
}

@Preview(name = "Search Bar Disabled", showBackground = true)
@Composable
private fun SearchBarDisabledPreview() {
    ArtistsAppTheme {
        SearchBar(
            query = "",
            onQueryChange = { },
            onClearClick = { },
        )
    }
}

@Preview(name = "Artist Item", showBackground = true)
@Composable
private fun ArtistItemPreview(
    @PreviewParameter(ArtistPreviewParameterProvider::class) artist: Artist
) {
    ArtistsAppTheme {
        ArtistItem(
            artist = artist,
            onClick = { }
        )
    }
}

@Preview(name = "Artist Item - Group", showBackground = true)
@Composable
private fun ArtistItemGroupPreview() {
    ArtistsAppTheme {
        ArtistItem(
            artist = Artist(
                id = 1,
                name = "Radiohead",
                type = "group",
                imageUrl = "https://img.discogs.com/radiohead.jpg",
                resourceUrl = "https://api.discogs.com/artists/1"
            ),
            onClick = { }
        )
    }
}

@Preview(name = "Artist Item - Person", showBackground = true)
@Composable
private fun ArtistItemPersonPreview() {
    ArtistsAppTheme {
        ArtistItem(
            artist = Artist(
                id = 2,
                name = "Thom Yorke",
                type = "person",
                imageUrl = "https://img.discogs.com/thom.jpg",
                resourceUrl = "https://api.discogs.com/artists/2"
            ),
            onClick = { }
        )
    }
}

@Preview(name = "Artist Item - No Image", showBackground = true)
@Composable
private fun ArtistItemNoImagePreview() {
    ArtistsAppTheme {
        ArtistItem(
            artist = Artist(
                id = 3,
                name = "The Beatles with a very long name that should truncate",
                type = "group",
                imageUrl = null,
                resourceUrl = "https://api.discogs.com/artists/3"
            ),
            onClick = { }
        )
    }
}

@Preview(name = "Artist List", showBackground = true)
@Composable
private fun ArtistListPreview() {
    ArtistsAppTheme {
        LazyColumn(
            contentPadding = PaddingValues(vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(3) { index ->
                val sampleArtists = listOf(
                    Artist(
                        id = 1,
                        name = "Radiohead",
                        type = "group",
                        imageUrl = "https://img.discogs.com/radiohead.jpg",
                        resourceUrl = "https://api.discogs.com/artists/1"
                    ),
                    Artist(
                        id = 2,
                        name = "Thom Yorke",
                        type = "person",
                        imageUrl = "https://img.discogs.com/thom.jpg",
                        resourceUrl = "https://api.discogs.com/artists/2"
                    ),
                    Artist(
                        id = 3,
                        name = "The Beatles",
                        type = "group",
                        imageUrl = null,
                        resourceUrl = "https://api.discogs.com/artists/3"
                    )
                )
                ArtistItem(
                    artist = sampleArtists[index],
                    onClick = { }
                )
            }
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ArtistSearchContentDarkPreview() {
    ArtistsAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.md)
        ) {
            SearchBar(
                query = "Radiohead",
                onQueryChange = { },
                onClearClick = { },
            )

            Spacer(modifier = Modifier.height(spacing.md))

            ArtistItem(
                artist = Artist(
                    id = 1,
                    name = "Radiohead",
                    type = "group",
                    imageUrl = "https://img.discogs.com/radiohead.jpg",
                    resourceUrl = "https://api.discogs.com/artists/1"
                ),
                onClick = { }
            )
        }
    }
}

@Preview(name = "Landscape", showBackground = true, device = "spec:width=640dp,height=360dp")
@Composable
private fun ArtistSearchContentLandscapePreview() {
    ArtistsAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.md)
        ) {
            SearchBar(
                query = "",
                onQueryChange = { },
                onClearClick = { },
            )

            Spacer(modifier = Modifier.height(spacing.md))

            EmptyState(
                icon = Icons.Default.Search,
                title = stringResource(R.string.empty_state_title),
                subtitle = stringResource(R.string.empty_state_subtitle)
            )
        }
    }
}