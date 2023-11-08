package com.fesvieira.pomotasks.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme as mtc
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun AppFloatActionButton(icon: Painter, onClick: () -> Unit) {
    FloatingActionButton(
        containerColor = mtc.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            tint = mtc.onSecondaryContainer,
            contentDescription = null,
        )
    }
}