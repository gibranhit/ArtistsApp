package com.gibran.artistsapp.presentation.ui.components

import android.R.id.message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gibran.artistsapp.R
import com.gibran.artistsapp.ui.theme.componentSizes
import com.gibran.artistsapp.ui.theme.cornerRadius
import com.gibran.artistsapp.ui.theme.spacing

// MARK: - Loading Components

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: LoadingSize = LoadingSize.Medium
) {
    val indicatorSize = when (size) {
        LoadingSize.Small -> componentSizes.loadingIndicatorSm
        LoadingSize.Medium -> componentSizes.loadingIndicatorMd
        LoadingSize.Large -> componentSizes.loadingIndicatorLg
    }

    CircularProgressIndicator(
        modifier = modifier.size(indicatorSize),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = when (size) {
            LoadingSize.Small -> spacing.xs / 2
            LoadingSize.Medium -> spacing.xs / 2
            LoadingSize.Large -> spacing.xs
        }
    )
}

@Composable
fun LoadingContent(
    message: String = stringResource(R.string.loading_message),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingIndicator(size = LoadingSize.Large)
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingMoreIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.md),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(size = LoadingSize.Medium)
    }
}

// MARK: - Empty State Components

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Search,
    title: String,
    subtitle: String? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(spacing.lg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(componentSizes.iconXxl),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    message: String,
    title: String = stringResource(R.string.error_title),
    onRetry: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(cornerRadius.md)
        ) {
            Column(
                modifier = Modifier.padding(spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(componentSizes.iconMd)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                SecondaryButton(
                    text = stringResource(R.string.retry_button),
                    onClick = onRetry
                )
            }
        }
    }
}

@Preview
@Composable
fun ErrorCardPreview() {
    ErrorCard(
        title = "Error Title",
        message = "This is a long error message that should wrap to multiple lines if necessary.",
        onRetry = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(cornerRadius.md)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = spacing.md,
                vertical = spacing.sm
            ),
            color = if (enabled) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f
            ),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

enum class LoadingSize {
    Small, Medium, Large
}