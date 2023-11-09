package com.fesvieira.pomotasks.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme as mtc
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fesvieira.pomotasks.R

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

@Preview
@Composable
fun PreviewAppFloatActionButton() {
    AppFloatActionButton(icon = painterResource(R.drawable.ic_add)) {}
}