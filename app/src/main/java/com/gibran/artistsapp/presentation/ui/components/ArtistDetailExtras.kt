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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistCarousel(images: List<Image>, modifier: Modifier = Modifier) {
    val displayImages = images.ifEmpty {
        listOf(Image("placeholder"))
    }

    val pagerState = rememberPagerState { displayImages.size }

    LaunchedEffect(pagerState) {
        if (images.isNotEmpty() && images.size > 1) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % displayImages.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 800)
                )
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            // Parallax effect
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale by animateFloatAsState(
                targetValue = if (pageOffset.absoluteValue < 1f) 1f - (pageOffset.absoluteValue * 0.1f) else 0.9f,
                animationSpec = tween(300), label = ""
            )
            val alpha by animateFloatAsState(
                targetValue = if (pageOffset.absoluteValue < 1f) 1f - (pageOffset.absoluteValue * 0.3f) else 0.7f,
                animationSpec = tween(300), label = ""
            )

            if (displayImages[page].uri == "placeholder") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Sin imagen",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                AsyncImage(
                    model = displayImages[page].uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(displayImages.size) { index ->
                val selected = pagerState.currentPage == index
                val indicatorScale by animateFloatAsState(
                    targetValue = if (selected) 1.2f else 1f,
                    animationSpec = tween(300), label = ""
                )
                Box(
                    modifier = Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .graphicsLayer {
                            scaleX = indicatorScale
                            scaleY = indicatorScale
                        }
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.4f
                            )
                        )
                )
            }
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
        Column(Modifier.padding(16.dp)) {
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
                Spacer(Modifier.height(8.dp))
                content()
            }
        }
    }
}
