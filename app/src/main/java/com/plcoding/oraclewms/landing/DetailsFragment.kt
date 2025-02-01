package com.plcoding.oraclewms.landing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.login.CommandUiState

@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    response: CommandUiState,
    clickPosition: Int,
    startHome: Boolean? = false
) {
    BackHandler {
        navController.popBackStack()
        viewModel.sendCommand(
            Utils.deviceUUID(),
            Utils.getControlCharacterValueOptimized("Ctrl-W")
        )
    }

    val scanner = GmsBarcodeScanning.getClient(LocalContext.current)
    if (startHome == false)
        LaunchedEffect(true) {
            viewModel.sendCommand(
                Utils.deviceUUID(),
                "${clickPosition}\n"
            )
        }

    if (response is CommandUiState.Success) {
        response.response?.formFields.let {
            if (it == null) {
            } else ListScreen(it, scanner, modifier, viewModel)
        }
        response.response?.popups.let {
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
                                            Utils.deviceUUID(), Utils.getControlCharacterValueOptimized("Ctrl-A")
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
                Text(text = dialogTitle)
            },
            text = {
                //repeat(ups.size) {
                if (ups.get(0).type.equals("message")) {
                    Text(text = ups.get(0).content)
                } else {
                    WareHouseTextField(viewModel, ups.get(0)) {
                        text.value = it
                    }
                }

                //}
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
                        Text(it)
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
    OutlinedTextField(
        label = {
            Text(
                popup.content,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        },
        value = textObj.value,
        trailingIcon = {
        },
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
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
    val focusRequester = remember { FocusRequester() }
    val cursor = rememberSaveable { item.cursor }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        label = {
            Text(
                item?.form_key ?: "",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        },
        value = textObj?.value ?: "",
        trailingIcon = {
            if (item.bar_code)
                Icon(
                    Icons.Outlined.Star,
                    null,
                    modifier = Modifier
                        .clickable {
                            scanner
                                .startScan()
                                .addOnSuccessListener { barcode ->
                                    println("barcode")
                                    println(barcode.rawValue)
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
}

