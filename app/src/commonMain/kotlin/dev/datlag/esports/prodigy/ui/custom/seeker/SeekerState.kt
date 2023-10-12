package dev.datlag.esports.prodigy.ui.custom.seeker

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * A state object which can be hoisted to observe the current segment of Seeker. In most cases this
 * will be created by [rememberSeekerState]
 * */
@Stable
class SeekerState() {

    /**
     * The current segment corresponding to the current seeker value.
     * */
    var currentSegment: Segment by mutableStateOf(Segment.Unspecified)

    internal var onDrag: ((Float) -> Unit)? = null

    internal val draggableState = DraggableState {
        onDrag?.invoke(it)
    }

    internal fun currentSegment(
        value: Float,
        segments: List<Segment>
    ) = (segments.findLast { value >= it.start } ?: Segment.Unspecified).also { this.currentSegment = it }
}

/**
 * Creates a SeekerState which will be remembered across compositions.
 * */
@Composable
fun rememberSeekerState(): SeekerState = remember {
    SeekerState()
}

/**
 * A class to hold information about a segment.
 * @param name name of the segment
 * @param start the value at which this segment should start in the track. This should must be in the
 * range of the Seeker range values.
 * @param color the color of the segment
 * */
@Immutable
data class Segment(
    val name: String,
    val start: Float,
    val color: Color = Color.Unspecified
) {
    companion object {
        val Unspecified = Segment(name = "", start = 0f)
    }
}

@Immutable
internal data class SegmentPxs(
    val name: String,
    val startPx: Float,
    val endPx: Float,
    val color: Color
)