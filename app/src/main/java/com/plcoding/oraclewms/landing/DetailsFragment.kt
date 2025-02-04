package com.plcoding.oraclewms.landing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.MenuItem
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.login.CommandUiState
import com.plcoding.oraclewms.login.LoaderScreen
import com.plcoding.oraclewms.login.LoginViewModel
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Calendar
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.plcoding.oraclewms.SharedPref
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    state: CommandUiState,
    item: MenuItem?
) {
    BackHandler {
        navController.popBackStack()
        viewModel.sendCommand(
            Utils.deviceUUID(),
            Utils.getControlCharacterValueOptimized("Ctrl-W")
        )
    }
    val context = LocalContext.current
    val scanner = GmsBarcodeScanning.getClient(context)

    if (state is CommandUiState.Success && state.response?.menuItems?.isEmpty() == false)
        LaunchedEffect(true) {
            viewModel.sendCommand(
                Utils.deviceUUID(),
                "${item?.optionNumber}\n"
            )
        }
    ListScreen(scanner, modifier, viewModel, item?.optionName)
    if (state is CommandUiState.Success) {
        state.response?.formFields.let {
        }
        state.response?.popups.let {
            it.let { ups ->
                if (ups.isNullOrEmpty()) {
                } else {
                    val showDialog = remember { mutableStateOf(true) }
                    if (showDialog.value) {
                        DialogWithMsg( {},
                            onConfirmation = {
                                if (ups.isNotEmpty()) {
                                    val firstUp = ups.first()
                                    if (!firstUp.type.equals("message")) {
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
                            viewModel = viewModel,
                            ups = ups.first(),
                            showDialog = showDialog,
                            false
                        )
                    }
                }
            }
        }
    } else if (state is CommandUiState.Error) {
        val context = LocalContext.current
        if (state.code == HttpURLConnection.HTTP_NOT_FOUND) {
            val showDialog = remember { mutableStateOf(true) }
            DialogWithMsg({},
                onConfirmation = {
                    viewModel.startActivity(context)
                    showDialog.value = false
                },
                viewModel = viewModel,
                ups = Popup("Your session expired. Please login again", "message"),
                showDialog = showDialog,
                false
            )
        } else {
        }
    } else if (state is CommandUiState.Loading) {
        LoaderScreen()
    } else {

    }
}
fun checkPermission(context: Context, permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
}
@Composable
fun DialogWithMsg(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    viewModel: LoginViewModel,
    ups: Popup,
    showDialog: MutableState<Boolean>,
    error: Boolean
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
                text = if (ups.type.equals("message")) "Alert" else "Enter Input",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (ups.type.equals("message")) Color.Red else Color.Blue)
                    .padding(10.dp),
                textAlign = TextAlign.Left,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(15.dp))
            if (ups.type.equals("message")) {
                Text(
                    text = ups.content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                )
            } else {
                Text(
                    ups.content,
                    modifier = Modifier.padding(start = 15.dp),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                )
                WareHouseTextField(viewModel) {
                    text.value = it
                }
            }
            Spacer(modifier = Modifier.padding(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (error) TextButton(
                    onClick = {
                       onDismissRequest()
                        showDialog.value = false
                    }
                ) {
                    Text(
                        "Dismiss",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                    )
                }
                TextButton(
                    onClick = {
                        onConfirmation(text.value)
                        showDialog.value = false
                    }
                ) {
                    Text(
                        if (error)"Accept" else "Ok",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))

                    )
                }
            }
        }
    }
}

@Composable
fun WareHouseTextField(viewModel: LoginViewModel, onChange: (String) -> Unit) {
    val textObj = rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = textObj.value,
        singleLine = true,
        onValueChange = {
            onChange(it)
            textObj.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListScreen(
   scanner: GmsBarcodeScanner,
    modifier: Modifier,
    viewModel: LandingViewModel,
    optionName: String?
) {
    val permissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    Box {
        viewModel.formItems.let { item ->
            LazyColumn(
                modifier.background(color = if (isSystemInDarkTheme()) colorResource(R.color.terinary_dark_imws) else colorResource(R.color.terinary_imws)),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 5.dp, end = 5.dp),

            ) {
                item {
                    Column {
                        HorizontalDivider(Modifier.alpha(0.4f), 2.dp, color = if (isSystemInDarkTheme()) colorResource(R.color.terinary_dark_imws) else colorResource(R.color.terinary_imws))
                        Text(
                            text = if (optionName.isNullOrEmpty()) if(SharedPref.getScreenName().toString().isEmpty())  "Xpress WMS" else SharedPref.getScreenName().toString() else optionName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            fontSize = 15.sp,
                            color = if (isSystemInDarkTheme()) colorResource(R.color.secondary_dark_imws) else colorResource(R.color.secondary_imws)
                        )
                        HorizontalDivider(Modifier.alpha(0.4f), 2.dp, color = Color.Gray)
                    }
                }
                items(item.size) { x ->
                    ListItem(item = item.get(x), scanner, viewModel, permissionState)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListItem(
    item: FormField,
    scanner: GmsBarcodeScanner,
    viewModel: LandingViewModel,
    permissionState: PermissionState
) {
    Log.d("DetailsFragment", "ListItem: ${item.cursor} ${item.form_key}")
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
    var resultText by remember { mutableStateOf("No result yet") }

    val launcher = rememberLauncherForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val returnedString = data?.getStringExtra("returned_string") // Get the returned string
            if (returnedString != null) {
                textObj.value = returnedString // Update the result text
            }
        }
    }
    val showDate = rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    TextField(
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
                        .size(40.dp)
                        .clickable {
                            if (item.cursor)
                                if (permissionState.status.isGranted) {
                                    val intent = Intent(context, BarCodeActivity::class.java) // Replace SecondActivity
                                    launcher.launch(intent)
//                                    scanner
//                                        .startScan()
//                                        .addOnSuccessListener { barcode ->
//                                            println("barcode")
//                                            textObj.value = barcode.rawValue
//                                        }
//                                        .addOnCanceledListener {
//                                            println("barcode1")
//                                        }
//                                        .addOnFailureListener { e ->
//                                            println("barcode2")
//                                            println(e.printStackTrace())
//                                            e.printStackTrace()
//                                        }
                                } else {
                                    if (permissionState.status.shouldShowRationale)
                                    else {
                                        permissionState.launchPermissionRequest()
                                    }
                                }


//                            ModuleInstall.getClient(context).installModules(ModuleInstallRequest.newBuilder()
//                                .addApi(GmsBarcodeScanning.getClient(context))
//                                .build())
//                                .addOnSuccessListener { response ->
//                                    if (response.areModulesAlreadyInstalled()) {
//                                        // Module already installed, proceed with scanning
//                                        startScanning(context,textObj)
//                                    } else {
//                                        // Module was just installed, wait briefly then scan
//                                        Handler(Looper.getMainLooper()).postDelayed({
//                                            startScanning(context,textObj)
//                                        }, 1000)
//                                    }
//                                }
//                                .addOnFailureListener { e ->
//                                    // Handle installation failure
//                                }
                        }
                        .padding(5.dp)
                )
            else if (item.formatters?.format_date == true)
                if (item.cursor) Icon(
                    Icons.Outlined.DateRange,
                    null,
                    modifier = Modifier
                        .clickable {
                            showDate.value = true
                        }
                        .padding(5.dp)
                )
        },
        enabled = item.cursor,
        singleLine = true,
        onValueChange = { textObj.value = it },
        modifier = if (item.cursor) Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp)
            .focusRequester(focusRequester)
        else Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        keyboardActions = KeyboardActions(
            onNext = {
                textObj.value?.let {
                    viewModel.sendCommand(
                        Utils.deviceUUID(), it + "\t"
                    )
                }
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedLabelColor = Color.Gray
        )
    )
    if (item.cursor) LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
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

