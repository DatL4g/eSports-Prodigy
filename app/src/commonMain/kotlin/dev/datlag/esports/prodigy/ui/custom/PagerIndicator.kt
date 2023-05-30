package dev.datlag.esports.prodigy.ui.custom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    state: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth().then(modifier),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            val color = if (state.currentPage == iteration) indicatorColor else indicatorColor.copy(alpha = 0.5f)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .width(24.dp)
                    .height(8.dp)

            )
        }
    }
}