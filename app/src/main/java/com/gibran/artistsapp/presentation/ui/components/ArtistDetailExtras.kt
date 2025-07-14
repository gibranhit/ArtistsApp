package com.gibran.artistsapp.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gibran.artistsapp.domain.model.Image
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue

private const val AUTO_SCROLL_DELAY_MS = 3000L
private const val SCROLL_ANIMATION_DURATION_MS = 800
private const val SCALE_ANIMATION_DURATION_MS = 300
private const val CAROUSEL_HEIGHT_DP = 300
private const val PARALLAX_SCALE_FACTOR = 0.1f
private const val PARALLAX_ALPHA_FACTOR = 0.3f
private const val MIN_SCALE = 0.9f
private const val MIN_ALPHA = 0.7f
private const val INDICATOR_SCALE_SELECTED = 1.2f
private const val INDICATOR_ALPHA_UNSELECTED = 0.4f
private const val ICON_ALPHA_PLACEHOLDER = 0.6f
private const val ICON_SIZE_DP = 80
private const val BORDER_RADIUS_DP = 12
private const val PADDING_DP = 16
private const val SPACING_DP = 8
private const val INDICATOR_SPACING_DP = 4
private const val INDICATOR_SIZE_SELECTED_DP = 8
private const val INDICATOR_SIZE_UNSELECTED_DP = 6

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistCarousel(images: List<Image>, modifier: Modifier = Modifier) {
    val displayImages = images.ifEmpty { listOf(Image("placeholder")) }
    val pagerState = rememberPagerState { displayImages.size }

    LaunchedEffect(pagerState) {
        if (images.isNotEmpty() && images.size > 1) {
            while (true) {
                delay(AUTO_SCROLL_DELAY_MS)
                val nextPage = (pagerState.currentPage + 1) % displayImages.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = SCROLL_ANIMATION_DURATION_MS)
                )
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(CAROUSEL_HEIGHT_DP.dp)
        ) { page ->
            CarouselPage(
                image = displayImages[page],
                pagerState = pagerState,
                page = page
            )
        }
        CarouselIndicators(
            pagerState = pagerState,
            imageCount = displayImages.size,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(PADDING_DP.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarouselPage(
    image: Image,
    pagerState: androidx.compose.foundation.pager.PagerState,
    page: Int
) {
    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
    val scale by animateFloatAsState(
        targetValue = if (pageOffset.absoluteValue < 1f) {
            1f - (pageOffset.absoluteValue * PARALLAX_SCALE_FACTOR)
        } else {
            MIN_SCALE
        },
        animationSpec = tween(SCALE_ANIMATION_DURATION_MS),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (pageOffset.absoluteValue < 1f) {
            1f - (pageOffset.absoluteValue * PARALLAX_ALPHA_FACTOR)
        } else {
            MIN_ALPHA
        },
        animationSpec = tween(SCALE_ANIMATION_DURATION_MS),
        label = "alpha"
    )

    val modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .clip(RoundedCornerShape(BORDER_RADIUS_DP.dp))

    if (image.uri == "placeholder") {
        PlaceholderImage(modifier)
    } else {
        AsyncImage(
            model = image.uri,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PlaceholderImage(modifier: Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Sin imagen",
            modifier = Modifier.size(ICON_SIZE_DP.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = ICON_ALPHA_PLACEHOLDER)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarouselIndicators(
    pagerState: androidx.compose.foundation.pager.PagerState,
    imageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(INDICATOR_SPACING_DP.dp)
    ) {
        repeat(imageCount) { index ->
            val selected = pagerState.currentPage == index
            val indicatorScale by animateFloatAsState(
                targetValue = if (selected) INDICATOR_SCALE_SELECTED else 1f,
                animationSpec = tween(SCALE_ANIMATION_DURATION_MS),
                label = "indicatorScale"
            )
            Box(
                modifier = Modifier
                    .size(if (selected) INDICATOR_SIZE_SELECTED_DP.dp else INDICATOR_SIZE_UNSELECTED_DP.dp)
                    .graphicsLayer {
                        scaleX = indicatorScale
                        scaleY = indicatorScale
                    }
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = INDICATOR_ALPHA_UNSELECTED)
                        }
                    )
            )
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = { expanded = !expanded }
    ) {
        Column(Modifier.padding(PADDING_DP.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = null
                )
            }
            if (expanded) {
                Spacer(Modifier.height(SPACING_DP.dp))
                content()
            }
        }
    }
}
