package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.R
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@Composable
fun AppFloatActionButton(
    icon: Painter,
    isAnimating: Boolean = false,
    onClick: () -> Unit
) {
    var inflate by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (inflate) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "scale"
    )

    LaunchedEffect(isAnimating) {
        while (isAnimating) {
            inflate = true
            delay(1000)
            inflate = false
            delay(1000)
        }
        inflate = false
    }

    FloatingActionButton(
        containerColor = mtc.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
    ) {
        Icon(
            painter = icon,
            tint = mtc.onSecondaryContainer,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun PreviewAppFloatActionButton() {
    AppFloatActionButton(
        icon = painterResource(R.drawable.ic_add),
        isAnimating = true
    ) {}
}