package com.jason.scrollpicker.ui.picker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jason.scrollpicker.util.DateUtil.formatMonth
import com.jason.scrollpicker.util.noRippleClickable
import com.jason.scrollpicker.util.roundTo
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollPicker(
    modifier: Modifier = Modifier,
    firstOptionSelected: Int = 0,
    secondOptionSelected: Int = 0,
    thirdOptionSelected: Int = 0,
    firstOptions: List<BaseScrollOption> = emptyList(),
    secondOptions: List<BaseScrollOption> = emptyList(),
    thirdOptions: List<BaseScrollOption> = emptyList(),
    paddingValues: PaddingValues = PaddingValues(16.dp),
    properties: PickerProperties = PickerProperties(),
    onItemSelected: (BaseScrollOption, BaseScrollOption, BaseScrollOption) -> Unit
) {
    val density = LocalDensity.current

    val firstListState = rememberLazyListState()
    val secondListState = rememberLazyListState()
    val thirdListState = rememberLazyListState()

    val itemHeight = properties.itemHeight
    val itemCount = properties.itemCount

    var heightOfView by remember { mutableStateOf(0.dp) }
    val contentPadding by remember { mutableStateOf(itemHeight * (itemCount / 2)) }

    var firstPicked by remember { mutableStateOf(BaseScrollOption()) }
    var secondPicked by remember { mutableStateOf(BaseScrollOption()) }
    var thirdPicked by remember { mutableStateOf(BaseScrollOption()) }

    val coroutineScope = rememberCoroutineScope()

    var isFirstViewDidLoad by rememberSaveable { mutableStateOf(false) }
    var isSecondViewDidLoad by rememberSaveable { mutableStateOf(false) }
    var isThirdViewDidLoad by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(firstOptions) {
        if (!isFirstViewDidLoad) {
            if (firstOptions.isNotEmpty()) {
                val firstIndex = firstOptions.indexOfFirst { it.label == firstOptionSelected.toString() }
                firstListState.scrollToItem(if (firstIndex == -1) 0 else firstIndex)
                isFirstViewDidLoad = true
            }
        }
    }

    LaunchedEffect(secondOptions) {
        if (!isSecondViewDidLoad) {
            if (secondOptions.isNotEmpty()) {
                val index = secondOptions.indexOfFirst { formatMonth(it.label) == secondOptionSelected }
                secondListState.scrollToItem(if (index == -1) 0 else index)
                isSecondViewDidLoad = true
            }
        }
    }

    LaunchedEffect(thirdOptions) {
        if (!isThirdViewDidLoad) {
            if (thirdOptions.isNotEmpty()) {
                val index = thirdOptions.indexOfFirst { it.label == thirdOptionSelected.toString() }
                thirdListState.scrollToItem(if (index == -1) 0 else index)
                isThirdViewDidLoad = true
            }
        }
    }

    LaunchedEffect(firstPicked, secondPicked, thirdPicked) {
        if (isFirstViewDidLoad && isSecondViewDidLoad && isThirdViewDidLoad) {
            onItemSelected(firstPicked, secondPicked, thirdPicked)
        }
    }

    @Composable
    fun calculateScaleSize(
        state: LazyListState,
        index: Int
    ): Float {
        return remember {
            derivedStateOf {
                val currentItemInfo =
                    state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                        ?: return@derivedStateOf 0.5f

                (1f - minOf(0.5f, abs(currentItemInfo.offset) / heightOfView.value))
            }
        }.value
    }

    @Composable
    fun getTextStyle(progress: Float): TextStyle {
        val animation = remember {
            Animatable(0f)
        }

        LaunchedEffect(progress) {
            coroutineScope.launch {
                if (progress >= 0.8f) {
                    animation.animateTo(1f, tween(70))
                } else {
                    animation.animateTo(0f, tween(70))
                }
            }
        }

        return remember(animation.value) {
            derivedStateOf {
                lerp(properties.unselectedTextStyle, properties.selectedTextStyle, animation.value)
            }
        }.value
    }

    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(properties.shapeRadius))
            .background(color = properties.backgroundColor)
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * itemCount)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * itemCount)
                    .background(color = properties.backgroundColor)
                    .onGloballyPositioned {
                        heightOfView = with(density) { it.size.height.toDp() }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (firstOptions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = properties.shapeSelectedBoxRadius,
                                        bottomStart = properties.shapeSelectedBoxRadius
                                    )
                                )
                                .background(color = properties.backgroundSelectedBox)
                                .align(Alignment.Center)
                        )
                        LazyColumn(
                            state = firstListState,
                            contentPadding = PaddingValues(vertical = contentPadding),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = firstListState),
                        ) {
                            items(
                                count = firstOptions.size,
                                itemContent = { index ->
                                    val progress =
                                        calculateScaleSize(state = firstListState, index = index)

                                    if (progress.toDouble().roundTo(1) == 1.0) {
                                        firstPicked = firstOptions[index]
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(itemHeight)
                                            .fillMaxWidth()
                                            .noRippleClickable {
                                                coroutineScope.launch {
                                                    firstListState.animateScrollToItem(index)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = firstOptions[index % firstOptions.size].label,
                                            style = getTextStyle(progress),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                if (secondOptions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .background(color = properties.backgroundSelectedBox)
                                .align(Alignment.Center)
                        )
                        LazyColumn(
                            state = secondListState,
                            contentPadding = PaddingValues(vertical = contentPadding),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = secondListState),
                        ) {
                            items(
                                count = secondOptions.size,
                                itemContent = { index ->
                                    val progress =
                                        calculateScaleSize(state = secondListState, index = index)

                                    if (progress == 1f) {
                                        secondPicked = secondOptions[index]
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(itemHeight)
                                            .fillMaxWidth()
                                            .noRippleClickable {
                                                coroutineScope.launch {
                                                    secondListState.animateScrollToItem(index)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = secondOptions[index].label,
                                            style = getTextStyle(progress),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                if (thirdOptions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .clip(
                                    shape = RoundedCornerShape(
                                        topEnd = properties.shapeSelectedBoxRadius,
                                        bottomEnd = properties.shapeSelectedBoxRadius
                                    )
                                )
                                .background(color = properties.backgroundSelectedBox)
                                .align(Alignment.Center)
                        )
                        LazyColumn(
                            state = thirdListState,
                            contentPadding = PaddingValues(vertical = contentPadding),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = thirdListState),
                        ) {
                            items(
                                count = thirdOptions.size,
                                itemContent = { index ->
                                    val progress =
                                        calculateScaleSize(state = thirdListState, index = index)

                                    if (progress.toDouble().roundTo(1) == 1.0) {
                                        thirdPicked = thirdOptions[index]
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(itemHeight)
                                            .fillMaxWidth()
                                            .noRippleClickable {
                                                coroutineScope.launch {
                                                    thirdListState.animateScrollToItem(index)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = thirdOptions[index % thirdOptions.size].label,
                                            style = getTextStyle(progress),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

open class BaseScrollOption(
    open val label: String = ""
)

data class PickerProperties(
    val itemHeight: Dp = 40.dp,
    val itemCount: Int = 5,
    val selectedTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Black
    ),
    val unselectedTextStyle: TextStyle = TextStyle(
        fontSize = 13.sp,
        color = Color.Gray
    ),
    val backgroundSelectedBox: Color = Color.Gray.copy(0.5f),
    val backgroundColor: Color = Color.White,
    val shapeRadius: Dp = 8.dp,
    val shapeSelectedBoxRadius: Dp = 4.dp
)

