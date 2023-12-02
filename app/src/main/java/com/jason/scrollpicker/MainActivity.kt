package com.jason.scrollpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.jason.scrollpicker.ui.picker.DateScrollPicker
import com.jason.scrollpicker.ui.theme.ScrollpickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrollpickerTheme {
                ExampleForDatePicker()
            }
        }
    }
}

@Composable
private fun ExampleForDatePicker() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showSheet = remember { mutableStateOf(false) }
        val showDialog = remember { mutableStateOf(false) }

        Button(onClick = { showSheet.value = true }) {
            Text("BottomSheet")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog.value = true }) {
            Text("Dialog")
        }

        BottomSheetExample(isDisplay = showSheet)

        DialogExample(isDisplay = showDialog)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetExample(
    isDisplay: MutableState<Boolean>
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    fun onDismiss() {
        isDisplay.value = false
    }

    if (isDisplay.value) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            DateScrollPicker()
        }
    }
}

@Composable
private fun DialogExample(
    isDisplay: MutableState<Boolean>,
) {
    if (isDisplay.value) {
        Dialog(
            onDismissRequest = { isDisplay.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            content = {
                ConstraintLayout(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.White)
                ) {
                    val (btnClose, datePicker, btnOk, btnCancel) = createRefs()

                    DateScrollPicker(
                        modifier = Modifier.constrainAs(datePicker) {
                            top.linkTo(btnClose.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp))
                            .constrainAs(btnCancel) {
                                top.linkTo(datePicker.bottom)
                                end.linkTo(btnOk.start, margin = 8.dp)
                            }
                            .clickable {
                                isDisplay.value = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Cancel")
                    }

                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .width(100.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            .constrainAs(btnOk) {
                                top.linkTo(datePicker.bottom)
                                end.linkTo(parent.end, margin = 16.dp)
                            }
                            .clickable {
                                isDisplay.value = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "OK")
                    }
                }
            }
        )
    }
}

