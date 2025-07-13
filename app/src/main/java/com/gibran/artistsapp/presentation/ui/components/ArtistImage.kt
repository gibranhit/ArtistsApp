package com.gibran.artistsapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.ui.theme.cornerRadius

@Composable
fun ArtistImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(componentSizes.artistImageMd)
            .clip(RoundedCornerShape(cornerRadius.md))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.size(componentSizes.artistImageMd),
                contentScale = ContentScale.Crop
            )
        }
    }
}
