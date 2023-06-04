package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.dp

val LeftRoundedShape = CircleShape.copy(
    topEnd = CornerSize(2.dp),
    bottomEnd = CornerSize(2.dp)
)

val RightRoundedShape = CircleShape.copy(
    topStart = CornerSize(2.dp),
    bottomStart = CornerSize(2.dp)
)