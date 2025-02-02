package com.plcoding.oraclewms.landing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose.AppTheme
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.login.CommandUiState
import com.plcoding.oraclewms.login.LoaderScreen
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    state: CommandUiState,
    clickPosition: Int
) {
    BackHandler {
        navController.popBackStack()
        viewModel.sendCommand(
            Utils.deviceUUID(),
            Utils.getControlCharacterValueOptimized("Ctrl-W")
        )
    }

    val scanner = GmsBarcodeScanning.getClient(LocalContext.current)
    if (state is CommandUiState.Success && state.response?.menuItems?.isEmpty() == false)
        LaunchedEffect(true) {
            viewModel.sendCommand(
                Utils.deviceUUID(),
                "${clickPosition}\n"
            )
        }
    if (state is CommandUiState.Success) {
        state.response?.formFields.let {
            if (it == null) {
            } else ListScreen(it, scanner, modifier, viewModel)
        }
        state.response?.popups.let {
            it.let { ups ->
                if (ups.isNullOrEmpty()) {
                } else {
                    val showDialog = remember { mutableStateOf(true) }
                    if (showDialog.value) {
                        popDialog(
                            onConfirmation = {
                                if (ups.isNotEmpty()) {
                                    val firstUp = ups.first()
                                    if (firstUp.type != "message") {
                                        viewModel.sendCommand(
                                            Utils.deviceUUID(), it + "\t"
                                        )
                                    } else {
                                        viewModel.sendCommand(
                                            Utils.deviceUUID(),
                                            Utils.getControlCharacterValueOptimized("Ctrl-A")
                                        )
                                    }
                                } else {
                                    showDialog.value = false
                                }
                            },
                            dialogTitle = "Alert",
                            yes = "Ok",
                            viewModel = viewModel,
                            ups = ups,
                            showDialog = showDialog
                        )
                    }
                }
            }
        }
    } else if (state is CommandUiState.Error) {
        if (state.code == HttpURLConnection.HTTP_NOT_FOUND) viewModel.startActivity(LocalContext.current)
        else {
        }
    } else if (state is CommandUiState.Loading){
        LoaderScreen()
    } else {

    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    val viewModel = viewModel<LandingViewModel>()
    val showDialog = remember { mutableStateOf(true) }
    AppTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            DialogWithMsg({}, "Enter Text", "yes", viewModel,
                listOf(Popup("XYZ", "")), showDialog
            )
        }
    }
}

@Composable
fun DialogWithMsg(
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    yes: String?,
    viewModel: LandingViewModel,
    ups: List<Popup>,
    showDialog: MutableState<Boolean>
) {
    val text = rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = dialogTitle,
                modifier = Modifier.fillMaxWidth().background(Color.Red).padding(10.dp),
                textAlign = TextAlign.Left,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(15.dp))
            if (ups.get(0).type.equals("message")) {
                Text(
                    text = ups.get(0).content,
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                )
            } else {
                WareHouseTextField(viewModel, ups.get(0)) {
                    text.value = it
                }
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                TextButton(
                    onClick = {
                        onConfirmation(text.value)
                        showDialog.value = false
                    }
                ) {
                    yes?.let {
                        Text(
                            it,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_regular))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun popDialog(
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    yes: String?,
    viewModel: LandingViewModel,
    ups: List<Popup>,
    showDialog: MutableState<Boolean>
) {
    val text = rememberSaveable {
        mutableStateOf(
            ""
        )
    }

    if (showDialog.value) {
        AlertDialog(
            title = {
                Text(
                    text = dialogTitle,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_bold))
                )
            },
            text = {
                if (ups.get(0).type.equals("message")) {
                    Text(
                        text = ups.get(0).content,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                    )
                } else {
                    WareHouseTextField(viewModel, ups.get(0)) {
                        text.value = it
                    }
                }
            },
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation(text.value)
                        showDialog.value = false
                    }
                ) {
                    yes?.let {
                        Text(
                            it,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_regular))
                        )
                    }
                }
            }
//        properties = DialogProperties(false, false)
        )
    }
}

@Composable
fun WareHouseTextField(viewModel: LandingViewModel, popup: Popup, onChange: (String) -> Unit) {
    val textObj = rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    val focusRequester = remember { FocusRequester() }
    TextField(
        label = {
            Text(
                popup.content,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        },
        value = textObj.value,
        singleLine = true,
        onValueChange = {
            onChange(it)
            textObj.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .focusRequester(focusRequester),
        keyboardActions = KeyboardActions(
            onNext = {
                textObj.value.let {
                    viewModel.sendCommand(
                        Utils.deviceUUID(), it + "\t"
                    )
                }
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
fun ListScreen(
    item: List<FormField>,
    scanner: GmsBarcodeScanner,
    modifier: Modifier,
    viewModel: LandingViewModel
) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(start = 5.dp, end = 5.dp)
    ) {
        items(item.size) { x ->
            ListItem(item = item.get(x), scanner, viewModel)
        }
    }
}

@Composable
fun ListItem(item: FormField, scanner: GmsBarcodeScanner, viewModel: LandingViewModel) {
    val textObj = rememberSaveable {
        mutableStateOf(
            item.form_value?.trim().let {
                if (it?.contains("<UL>", true) == true) {
                    it.replace("<UL>", "", true)
                } else {
                    it
                }
            }
        )
    }
    val showDate = rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val cursor = rememberSaveable { item.cursor }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        label = {
            Text(
                item.form_key ?: "",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        },
        value = textObj.value ?: "",
        trailingIcon = {
            if (item.formatters?.format_barcode == true)
                Icon(
                    painter = painterResource(R.drawable.scan),
                    null,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            if (item.cursor) scanner
                                .startScan()
                                .addOnSuccessListener { barcode ->
                                    println("barcode")
                                    textObj.value = barcode.rawValue
                                }
                                .addOnCanceledListener {
                                    println("barcode1")
                                }
                                .addOnFailureListener { e ->
                                    println("barcode2")
                                }
                        }
                        .padding(8.dp)
                )
            else if (item.formatters?.format_date == true)
                if (item.cursor) Icon(
                    Icons.Outlined.DateRange,
                    null,
                    modifier = Modifier
                        .clickable {
                            showDate.value = true
                        }
                        .padding(8.dp)
                )
        },
        enabled = cursor,
        singleLine = true,
        onValueChange = { textObj.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .focusRequester(focusRequester),
        keyboardActions = KeyboardActions(
            onNext = {
                ///focusManager.moveFocus(FocusDirection.Down)
                textObj.value?.let {
                    viewModel.sendCommand(
                        Utils.deviceUUID(), it + "\t"
                    )
                }
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
    if (showDate.value) DatePickerModal({
        textObj.value = it.let {
            if (it == null) ""
            else {
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTimeInMillis(it)
                val dateFormat = SimpleDateFormat("dd/MM/YYYY")
                dateFormat.format(calendar.time)
            }
        }
    }, {
        showDate.value = false
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(
                    "OK",
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_regular))
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_regular))
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

