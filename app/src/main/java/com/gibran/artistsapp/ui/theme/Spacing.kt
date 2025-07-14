package com.gibran.artistsapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 40.dp,
    val xxxl: Dp = 48.dp
)

data class ComponentSizes(
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 32.dp,
    val iconXl: Dp = 48.dp,
    val iconXxl: Dp = 64.dp,

    val artistImageSm: Dp = 40.dp,
    val artistImageMd: Dp = 56.dp,
    val artistImageLg: Dp = 80.dp,
    val artistImageXl: Dp = 120.dp,

    val albumCoverLarge: Dp = 300.dp,

    val buttonHeightSm: Dp = 32.dp,
    val buttonHeightMd: Dp = 40.dp,
    val buttonHeightLg: Dp = 48.dp,

    val inputHeightMd: Dp = 56.dp,
    val inputHeightLg: Dp = 64.dp,

    val loadingIndicatorSm: Dp = 20.dp,
    val loadingIndicatorMd: Dp = 24.dp,
    val loadingIndicatorLg: Dp = 32.dp,

    val screenPadding: Dp = 16.dp,
    val cardPadding: Dp = 12.dp,
    val itemSpacing: Dp = 8.dp,

    val minTouchTarget: Dp = 48.dp
)

data class CornerRadius(
    val xs: Dp = 4.dp,
    val sm: Dp = 6.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 12.dp,
    val xl: Dp = 16.dp
)

data class Elevation(
    val none: Dp = 0.dp,
    val sm: Dp = 2.dp,
    val md: Dp = 4.dp,
    val lg: Dp = 6.dp,
    val xl: Dp = 8.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalComponentSizes = staticCompositionLocalOf { ComponentSizes() }
val LocalCornerRadius = staticCompositionLocalOf { CornerRadius() }
val LocalElevation = staticCompositionLocalOf { Elevation() }

val spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

val componentSizes: ComponentSizes
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentSizes.current

val cornerRadius: CornerRadius
    @Composable
    @ReadOnlyComposable
    get() = LocalCornerRadius.current

val elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current

