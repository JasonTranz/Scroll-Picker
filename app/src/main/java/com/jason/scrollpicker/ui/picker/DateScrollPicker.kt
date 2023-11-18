package com.jason.scrollpicker.ui.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jason.scrollpicker.util.DateUtil

@Preview
@Composable
private fun DateScrollPicker(
    modifier: Modifier = Modifier,
    maxYear: Int = DateUtil.getCurrentYear(),
    allowSelectThroughOnCurrentDate: Boolean = true,
) {
    val days = remember { mutableStateOf<List<BaseScrollOption>>(emptyList()) }
    val months = remember { mutableStateOf<List<BaseScrollOption>>(emptyList()) }
    val years = remember { mutableStateOf<List<BaseScrollOption>>(emptyList()) }

    val daySelected = remember { mutableIntStateOf(0) }
    val yearSelected = remember { mutableIntStateOf(0) }
    val monthSelected = remember { mutableIntStateOf(0) }

    var isViewDidLoad by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(true) {
        if (!isViewDidLoad) {
            days.value = DateUtil.getDayOfMonth(
                yearSelected.intValue,
                monthSelected.intValue
            ).map { BaseScrollOption(it) }
            months.value = DateUtil.getMonths(yearSelected.intValue).map { BaseScrollOption(it) }
            years.value = DateUtil.getYears(maxYear).map { BaseScrollOption(it) }
            isViewDidLoad = true
        }
    }

    LaunchedEffect(yearSelected.intValue) {
        if (isViewDidLoad) {
            months.value = DateUtil.getMonths(yearSelected.intValue).map { BaseScrollOption(it) }
        }
    }

    LaunchedEffect(monthSelected.intValue) {
        if (isViewDidLoad) {
            days.value = DateUtil.getDayOfMonth(yearSelected.intValue, monthSelected.intValue).map {
                BaseScrollOption(it)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        ScrollPicker(
            firstOptions = days.value,
            secondOptions = months.value,
            thirdOptions = years.value,
            firstOptionSelected = daySelected.intValue,
            secondOptionSelected = monthSelected.intValue,
            thirdOptionSelected = yearSelected.intValue,
            onItemSelected = { first, second, third ->
                daySelected.intValue = first.label.toIntOrNull() ?: 0
                monthSelected.intValue = DateUtil.formatMonth(second.label)
                yearSelected.intValue = third.label.toIntOrNull() ?: 0
            }
        )
    }
}