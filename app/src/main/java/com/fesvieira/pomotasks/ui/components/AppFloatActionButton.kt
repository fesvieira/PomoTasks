package com.fesvieira.pomotasks.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.R
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme.colorScheme as mtc

@Composable
fun AppFloatActionButton(
    icon: Painter,
    isAnimating: Boolean = false,
    size: Dp = 60.dp,
    onClick: () -> Unit
) {
    var inflate by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (inflate) 1.2f else 1.0f,
        animationSpec = tween(2000),
        label = "scale"
    )

    LaunchedEffect(isAnimating) {
        while (isAnimating) {
            inflate = !inflate
            delay(2000)
        }
        inflate = false
    }

    FloatingActionButton(
        containerColor = mtc.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .scale(scale)
    ) {
        Icon(
            painter = icon,
            tint = mtc.onSecondaryContainer,
            contentDescription = null,
            modifier = Modifier.size(size / 2.5f)
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