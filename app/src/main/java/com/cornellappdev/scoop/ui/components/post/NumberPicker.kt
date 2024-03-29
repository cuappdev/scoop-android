package com.cornellappdev.scoop.ui.components.post

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Allows one to select numbers by swiping.
 */
@Composable
fun NumberPicker(
    state: MutableState<Int>,
    modifier: Modifier = Modifier,
    range: IntRange? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onStateChanged: (Int) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val numbersColumnHeight = 36.dp
    val halvedNumbersColumnHeight = numbersColumnHeight / 2
    val halvedNumbersColumnHeightPx =
        with(LocalDensity.current) { halvedNumbersColumnHeight.toPx() }

    fun animatedStateValue(offset: Float): Int =
        state.value - (offset / halvedNumbersColumnHeightPx).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        if (range != null) {
            val offsetRange = remember(state.value, range) {
                val value = state.value
                val first = -(range.last - value) * halvedNumbersColumnHeightPx
                val last = -(range.first - value) * halvedNumbersColumnHeightPx
                first..last
            }
            updateBounds(offsetRange.start, offsetRange.endInclusive)
        }
    }
    val coercedAnimatedOffset = animatedOffset.value % halvedNumbersColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentHeight()
            .width(IntrinsicSize.Max)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halvedNumbersColumnHeightPx
                                val coercedAnchors = listOf(
                                    -halvedNumbersColumnHeightPx,
                                    0f,
                                    halvedNumbersColumnHeightPx
                                )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    halvedNumbersColumnHeightPx * (target / halvedNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        state.value = animatedStateValue(endValue)
                        onStateChanged(state.value)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
    ) {
        // Can customize appearance in the content block of the column here.
        Row {
            // This box is responsible for displaying the numbers and
            // is the main NumberPicking swiping mechanic.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier
                ProvideTextStyle(textStyle) {
                    Label(
                        text = (animatedStateValue - 1).toString(),
                        modifier = baseLabelModifier
                            .offset(y = -halvedNumbersColumnHeight)
                            .alpha(coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                    )
                    Label(
                        text = animatedStateValue.toString(),
                        modifier = baseLabelModifier
                            .alpha(1 - abs(coercedAnimatedOffset) / halvedNumbersColumnHeightPx)
                    )
                    Label(
                        text = (animatedStateValue + 1).toString(),
                        modifier = baseLabelModifier
                            .offset(y = halvedNumbersColumnHeight)
                            .alpha(-coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                    )
                }
            }

            Icon(
                Icons.Filled.UnfoldMore,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 15.dp),
                contentDescription = "Swipe indicator"
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Divider(color = Color.Black, thickness = 2.dp)
    }
}

@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = 22.sp),
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {})
        }
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}