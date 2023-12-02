package com.jason.scrollpicker.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jason.scrollpicker.util.DateUtil

private const val DEFAULT_FORMAT: String = "dd/MM/yyyy"
private const val DEFAULT_DATE_SELECTED: String = "1/11/1970"

@Composable
fun DateScrollPicker(
    modifier: Modifier = Modifier,
    maxYear: Int = DateUtil.getCurrentYear(),
    format: String = DEFAULT_FORMAT,
    defaultDateSelected: String = DEFAULT_DATE_SELECTED,
    allowSelectThroughOnCurrentDate: Boolean = true,
    onDateSelected: ((BaseScrollOption, BaseScrollOption, BaseScrollOption) -> Unit)? = null,
    properties: PickerProperties = PickerProperties(),
) {
    val days = rememberSaveable { mutableStateOf<List<BaseScrollOption>>(emptyList()) }
    val months = rememberSaveable { mutableStateOf<List<BaseScrollOption>>(emptyList()) }
    val years = rememberSaveable { mutableStateOf<List<BaseScrollOption>>(emptyList()) }

    val daySelected = rememberSaveable { mutableIntStateOf(DateUtil.getCurrentDate()) }
    val yearSelected = rememberSaveable { mutableIntStateOf(DateUtil.getCurrentYear()) }
    val monthSelected = rememberSaveable { mutableIntStateOf(DateUtil.getCurrentMonth()) }

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

    LaunchedEffect(yearSelected.intValue, isViewDidLoad) {
        if (isViewDidLoad) {
            months.value = DateUtil.getMonths(
                year = yearSelected.intValue,
                allowSelectThroughOnCurrentDate = allowSelectThroughOnCurrentDate
            ).map { BaseScrollOption(it) }
        }
    }

    LaunchedEffect(monthSelected.intValue, isViewDidLoad) {
        if (isViewDidLoad) {
            days.value = DateUtil.getDayOfMonth(
                year = yearSelected.intValue,
                month = monthSelected.intValue,
                allowSelectThroughOnCurrentDate = allowSelectThroughOnCurrentDate
            ).map { BaseScrollOption(it) }
        }
    }

    ScrollPicker(
        modifier = modifier,
        firstOptions = days.value,
        secondOptions = months.value,
        thirdOptions = years.value,
        firstOptionSelected = daySelected.intValue,
        secondOptionSelected = monthSelected.intValue,
        thirdOptionSelected = yearSelected.intValue,
        onItemSelected = { first, second, third ->
            if (isViewDidLoad) {
                daySelected.intValue = first.label.toIntOrNull() ?: 0
                monthSelected.intValue = DateUtil.formatMonth(second.label)
                yearSelected.intValue = third.label.toIntOrNull() ?: 0
            }
            onDateSelected?.invoke(first, second, third)
        },
        properties = properties
    )
}