package com.gibran.artistsapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gibran.artistsapp.R
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.ui.theme.cornerRadius

@Composable
fun ArtistImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: ArtistImageSize = ArtistImageSize.Medium
) {
    val imageSize = when (size) {
        ArtistImageSize.Small -> componentSizes.artistImageSm
        ArtistImageSize.Medium -> componentSizes.artistImageMd
        ArtistImageSize.Large -> componentSizes.artistImageLg
        ArtistImageSize.ExtraLarge -> componentSizes.artistImageXl
    }

    val radius = when (size) {
        ArtistImageSize.Small -> cornerRadius.sm
        ArtistImageSize.Medium -> cornerRadius.md
        ArtistImageSize.Large -> cornerRadius.lg
        ArtistImageSize.ExtraLarge -> cornerRadius.xl
    }

    val iconSize = imageSize * 0.4f

    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(imageSize)
            .clip(RoundedCornerShape(radius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contentDescription
                    ?: stringResource(R.string.artist_image_placeholder_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(iconSize)
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription
                    ?: stringResource(R.string.artist_image_description),
                modifier = Modifier.size(imageSize),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Convenience composables for different sizes
@Composable
fun ArtistImageSmall(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    ArtistImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = ArtistImageSize.Small
    )
}

@Composable
fun ArtistImageMedium(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    ArtistImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = ArtistImageSize.Medium
    )
}

@Composable
fun ArtistImageLarge(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    ArtistImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = ArtistImageSize.Large
    )
}

@Composable
fun ArtistImageExtraLarge(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    ArtistImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = ArtistImageSize.ExtraLarge
    )
}

enum class ArtistImageSize {
    Small, Medium, Large, ExtraLarge
}