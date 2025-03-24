package com.plcoding.oraclewms.landing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.AddCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.CameraActivity
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.login.CommandUiState
import com.plcoding.oraclewms.login.LoaderScreen
import com.plcoding.oraclewms.login.LoginViewModel
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Calendar


@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    state: CommandUiState,
    optionNumber: Int?,
    imageCallBack : () -> Unit
) {
    Log.d("DetailsFragment", "Inside composable")
    BackHandler {
        navController.popBackStack()
        viewModel.sendCommand(
            Utils.deviceUUID(),
            Utils.getControlCharacterValueOptimized("Ctrl-X")
        )
    }
    if (state is CommandUiState.Success && state.response?.formFields?.isEmpty() == true && state.response.popups.isEmpty())
        LaunchedEffect(true) {
            viewModel.sendCommand(
                Utils.deviceUUID(),
                "${optionNumber}\n"
            )
        }
    ListScreen(modifier, viewModel, state, viewModel.loader, imageCallBack)
    if (state is CommandUiState.Success) {
        state.response?.formFields.let {
        }
        state.response?.popups.let {
            it.let { ups ->
                if (ups.isNullOrEmpty()) {
                } else {
                    val showDialog = remember { mutableStateOf(true) }
                    if (showDialog.value) {
                        DialogWithMsg(
                            {},
                            onConfirmation = {
                                if (ups.isNotEmpty()) {
                                    val firstUp = ups.first()
                                    if (!firstUp.type.equals("message")) {
                                        viewModel.sendCommand(
                                            Utils.deviceUUID(), it + "\t",nextField=false
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
            DialogWithMsg(
                {},
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

@OptIn(ExperimentalPermissionsApi::class)
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
    val permissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
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
                WareHouseTextField(viewModel, ups, permissionState) {
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
                        onConfirmation(
                            if (ups.fieldList?.first()?.field_formatters?.formatDate == true) {
                                val userFormat = SimpleDateFormat("dd/MM/yyyy")
                                val calendar: Calendar = Calendar.getInstance()
                                calendar.time = userFormat.parse(text.value)
                                val dateFormat = SimpleDateFormat(SharedPref.getDateFormat())
                                dateFormat.format(calendar.time)
                            } else text.value
                        )
                        showDialog.value = false
                    }
                ) {
                    Text(
                        if (error) "Accept" else "Ok",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WareHouseTextField(
    viewModel: LoginViewModel, up: Popup,
    permissionState: PermissionState,
    onChange: (String) -> Unit
) {
    val showDate = rememberSaveable { mutableStateOf(false) }
    val textObj = rememberSaveable(up) {
        mutableStateOf(up.fieldList?.first()?.form_value?.trim() ?: "")
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val returnedString = data?.getStringExtra("returned_string") // Get the returned string
            if (returnedString != null) {
                onChange(returnedString)
                textObj.value = returnedString // Update the result text
            }
        }
    }
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    TextField(
        value = textObj.value,
        singleLine = true,
        onValueChange = {
            onChange(it)
            textObj.value = it
        },
        trailingIcon = {
            up.fieldList?.first()?.field_formatters.let {
                Row {
                    if (it?.formatDate == true) {
                        Icon(
                            Icons.Outlined.DateRange,
                            null,
                            modifier = Modifier
                                .clickable {
                                    showDate.value = true
                                }
                                .padding(5.dp)
                        )
                    }
                    if (it?.format_barcode == true) {
                        Icon(
                            painter = painterResource(R.drawable.scan),
                            null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    if (permissionState.status.isGranted) {
                                        val intent = Intent(
                                            context,
                                            BarCodeActivity::class.java
                                        )
                                        launcher.launch(intent)
                                    } else {
                                        if (permissionState.status.shouldShowRationale)
                                        else {
                                            permissionState.launchPermissionRequest()
                                        }
                                    }
                                }
                                .padding(5.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .focusRequester(focusRequester),
        keyboardActions = KeyboardActions(
            onNext = {
                textObj.value.let {

                    viewModel.sendCommand(
                        Utils.deviceUUID(), it + "\t", nextField = false
                    )
                }
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    if (showDate.value) DatePickerModal({
        textObj.value = it.let {
            if (it == null) ""
            else {
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTimeInMillis(it)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                dateFormat.format(calendar.time)
            }
        }
        onChange(textObj.value)
    }, {
        showDate.value = false
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBottomSheet(
    quantity: String?,
    viewModel: LoginViewModel, onDismiss: () -> Unit, imageCallBack: () -> Unit, launchCamera: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    ModalBottomSheet (
        onDismissRequest = {
            onDismiss()
            showBottomSheet = false
        },
        sheetState = sheetState
    ) {
        Column {
            viewModel.images.let {
                LazyRow (modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    items(it.size){ index->
                        if (index == 0) {
                            Row  {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    null,
                                    modifier = Modifier
                                        .clickable {
                                            imageCallBack()
                                        }
                                        .size(60.dp).padding(end = 15.dp))
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    null,
                                    modifier = Modifier
                                        .clickable {
                                            launchCamera()
                                        }
                                        .size(60.dp).padding(end = 15.dp))
                            }
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it[index])
                                    .build(),
                                contentDescription = "icon",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.size(120.dp).clickable {  }.padding(5.dp)
                            )
                        }
                    }
                }
            }

            OutlinedButton(onClick = {
                if (viewModel.images.size == 1) Toast.makeText(context, "Please add images to upload", Toast.LENGTH_LONG).show()
                else {
                    viewModel.uploadImages(context, quantity)
                    onDismiss()
                    showBottomSheet = false
                }
            }, modifier = Modifier.padding(15.dp).fillMaxWidth().padding(10.dp,0.dp,10.dp,0.dp)) {
                Text("Upload", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 15.sp,
                    color = if (isSystemInDarkTheme()) colorResource(R.color.secondary_dark_imws) else colorResource(
                        R.color.secondary_imws
                    ))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListScreen(
    modifier: Modifier,
    viewModel: LandingViewModel,
    state: CommandUiState,
    loader: Boolean,
    imageCallBack: () -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val returnedString = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra("TYPE", Uri::class.java)
            } else {
                data?.getParcelableExtra("TYPE")
            }
            if (returnedString != null) viewModel.addImage(returnedString)
        }
    }
    var quantity : String? = null
    var showBottomSheet by remember { mutableStateOf(false) }
    Box {
        viewModel.formItems.let { item ->
            LazyColumn(
                modifier.background(
                    color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                        R.color.white
                    )
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 5.dp, end = 5.dp),
                ) {
                item {
                    Column {
                        HorizontalDivider(
                            Modifier.alpha(0.4f),
                            2.dp,
                            color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                                R.color.white
                            )
                        )
                        Text(
                            text = if (state is CommandUiState.Success) state.response?.screenName?.value.let { if (it.isNullOrEmpty()) "" else it } else "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            fontSize = 15.sp,
                            color = if (isSystemInDarkTheme()) colorResource(R.color.secondary_dark_imws) else colorResource(
                                R.color.secondary_imws
                            )
                        )
                        HorizontalDivider(Modifier.alpha(0.4f), 2.dp, color = Color.Gray)
                    }
                }
                items(item.size) {
                    if (item.get(it).type.equals("form_field")) ListItem(
                        item = item.get(it),
                        viewModel,
                        permissionState, {
                            quantity = it
                            showBottomSheet = true
                        }
                    )
                    else if (item.get(it).type.equals("menu_item")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                                        R.color.white
                                    )
                                )
                                .clickable {
                                    viewModel.sendCommand(
                                        Utils.deviceUUID(),
                                        "${item.get(it).option_number}\n"
                                    )
                                }, verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${item.get(it).option_number}.",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(5.dp),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.spacegrotesk_medium
                                        )
                                    ),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${item.get(it).option_name}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.spacegrotesk_medium
                                        )
                                    ),
                                    fontSize = 15.sp
                                )
                            }
                            HorizontalDivider(
                                Modifier
                                    .fillMaxWidth()
                                    .alpha(0.4f), 2.dp, Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = item.get(it).value.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            fontSize = 15.sp,
                            color = if (isSystemInDarkTheme()) colorResource(R.color.secondary_dark_imws) else colorResource(
                                R.color.secondary_imws
                            )
                        )
                    }
                }
            }
        }
        if (loader) LoaderScreen()
        if (showBottomSheet) ImageBottomSheet(quantity, viewModel, { showBottomSheet = false }, imageCallBack,
            {
                if (permissionState.status.isGranted) {
                    val intent = Intent(
                        context,
                        CameraActivity::class.java
                    )
                    launcher.launch(intent)
                } else {
                    if (permissionState.status.shouldShowRationale)
                    else {
                        permissionState.launchPermissionRequest()
                    }
                }
            })
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ListItem(
    item: FormField,
    viewModel: LandingViewModel,
    permissionState: PermissionState,
    onImageClick : (quantity: String?) -> Unit
) {
    val textObj = remember(item.form_value) {
        mutableStateOf(
            item.form_value?.trim()
        )
    }

    val launcher = rememberLauncherForActivityResult(
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
            Row {
                if (item.formatters?.format_barcode == true) {
                    Icon(
                        painter = painterResource(R.drawable.scan),
                        null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (item.cursor)
                                    if (permissionState.status.isGranted) {
                                        val intent = Intent(
                                            context,
                                            BarCodeActivity::class.java
                                        )
                                        launcher.launch(intent)
                                    } else {
                                        if (permissionState.status.shouldShowRationale)
                                        else {
                                            permissionState.launchPermissionRequest()
                                        }
                                    }
                            }
                            .padding(5.dp)
                    )
                }
                if (item.formatters?.format_date == true) {
                    Icon(
                        Icons.Outlined.DateRange,
                        null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (item.cursor)
                                    showDate.value = true
                            }
                            .padding(5.dp))
                }
                if (item.formatters?.format_label == true)
                    Icon(
                        Icons.Outlined.AddCard,
                        null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (item.cursor) {
                                    val dev =
                                        Gson().fromJson(SharedPref.getEnv(), Dev::class.java)
                                    if (!SharedPref.getShipmentID().isNullOrEmpty()) {
                                        viewModel.fetchUserDetails(
                                            dev,
                                            SharedPref.getEnvValue(),
                                            SharedPref.getLoggedIn(),
                                            3,
                                            SharedPref.getLoggedPwd(),
                                            "ib_shipment/?shipment_nbr=${SharedPref.getShipmentID()}&values_list=company_id__code",
                                            item.form_key
                                        )
                                    }

                                }
                            }
                            .padding(5.dp)
                    )
                if (item.form_key.equals("Qty")) Icon(
                    Icons.Default.Photo,
                    null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            if (item.cursor) {
                                onImageClick(textObj.value?.trim())
                            }
                        }
                        .padding(5.dp)
                )
            }
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
                        Utils.deviceUUID(),
                        if (item.form_value?.trim()
                                .equals(it) && item.flag == true
                        ) "\t" else it + "\t",
                        item.form_key
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
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
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

