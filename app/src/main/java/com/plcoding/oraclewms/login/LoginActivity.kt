package com.plcoding.oraclewms.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.home.LandingActivity
import com.plcoding.oraclewms.landing.BarCodeActivity
import com.plcoding.oraclewms.landing.DialogWithMsg


class LoginActivity : ComponentActivity() {

    val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {
            Toast.makeText(
                this,
                "Please provide " +
                        "notification permission to get started",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel = viewModel<LoginViewModel>()
                val modifier = Modifier.fillMaxSize()
                Surface(
                    modifier,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Greeting(viewModel, viewModel.shellState, viewModel.cmdState, modifier)
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Greeting(
        viewModel: LoginViewModel,
        shellState: ShellUiState,
        cmdState: CommandUiState,
        modifier: Modifier
    ) {
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val environment = rememberSaveable { mutableStateOf("dev") }
        var passwordVisible by remember { mutableStateOf(false) }
        val checkState = remember { mutableStateOf(false) }
        val envs: ArrayList<Dev> = Gson().fromJson(
            SharedPref.getEnvResponse(),
            object : TypeToken<ArrayList<Dev>>() {}.type
        )
        val context = LocalContext.current

        val permissionState = rememberPermissionState(
            Manifest.permission.CAMERA
        )

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val returnedString = data?.getStringExtra("returned_string") // Get the returned string
                val type = data?.getStringExtra("type")
                if (returnedString != null) {
                    when (type) {
                        "email" -> email.value = returnedString
                        "password" -> password.value = returnedString
                    }
                }
            }
        }

        val showDialog = remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            viewModel.endShell(Utils.deviceUUID(), this@LoginActivity, "LoginActivity")
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(
                if (isSystemInDarkTheme()) colorResource(R.color.primary_dark_imws) else colorResource(
                    R.color.primary_imws
                )
            )
        ) {
            Column {
                Text(
                    "Xpress WMS", fontFamily = FontFamily(Font(R.font.jersey_normal)),
                    style = TextStyle(
                        color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                            R.color.white
                        ), fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.padding(15.dp),
                    colors = CardDefaults.cardColors(
                        if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                            R.color.white
                        )
                    )
                ) {
                    Column {
                        Text(
                            "Login", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Column {
                            OutlinedTextField(
                                label = {
                                    Text(
                                        "Environment",
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                    )
                                },
                                value = environment.value,
                                trailingIcon = {
                                    Icon(
                                        Icons.Outlined.ArrowDropDown,
                                        null,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                },
                                enabled = false,
                                singleLine = true,
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        checkState.value = true
                                    }
                                    .padding(15.dp, 15.dp, 15.dp, 5.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                            if (checkState.value) SpinnerSample(
                                envs, envs.first(),
                                onSelectionChanged = {
                                    environment.value = it.name
                                    checkState.value = false
                                }, Modifier.fillMaxWidth()
                            )
                        }
                        OutlinedTextField(
                            label = {
                                Text(
                                    "UserName",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                )
                            },
                            value = email.value,
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.scan),
                                    null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            if (!checkPermission(context, Manifest.permission.CAMERA)) {
                                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            } else {
                                                if (permissionState.status.isGranted) {
                                                    val intent = Intent(
                                                        context,
                                                        BarCodeActivity::class.java
                                                    ).apply {
                                                        putExtra("TYPE", "email")
                                                    }
                                                    launcher.launch(intent)
                                                } else {
                                                    if (permissionState.status.shouldShowRationale)
                                                    else {
                                                        permissionState.launchPermissionRequest()
                                                    }
                                                }
                                            }
                                        }
                                        .padding(5.dp))
                            },
                            singleLine = true,
                            onValueChange = { email.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp, 15.dp, 15.dp, 5.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                        OutlinedTextField(
                            label = {
                                Text(
                                    "Password",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                Row {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    val description =
                                        if (passwordVisible) "Hide password" else "Show password"
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(imageVector = image, description)
                                    }
                                    Icon(
                                        painter = painterResource(R.drawable.scan),
                                        null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                if (!checkPermission(context, Manifest.permission.CAMERA)) {
                                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                                } else {
                                                    if (permissionState.status.isGranted) {
                                                        val intent = Intent(
                                                            context,
                                                            BarCodeActivity::class.java
                                                        ).apply {
                                                            putExtra("TYPE", "password")
                                                        }
                                                        launcher.launch(intent)
                                                    } else {
                                                        if (permissionState.status.shouldShowRationale)
                                                        else {
                                                            permissionState.launchPermissionRequest()
                                                        }
                                                    }
                                                }
                                            }
                                            .padding(5.dp))
                                }
                            },
                            value = password.value,
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            },
                            singleLine = true,
                            onValueChange = { password.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                    }
                }

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                            R.color.white
                        )
                    )

                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemInDarkTheme()) colorResource(
                                    R.color.primary_dark_imws
                                ) else colorResource(R.color.primary_imws)
                            ),
                            onClick = {
                                viewModel.startShell(
                                    Utils.deviceUUID(),
                                    environment.value,
                                    email,
                                    password
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Text(
                                "Login", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }
        handleResponse(
            password,
            email,
            envs,
            environment,
            viewModel,
            shellState,
            cmdState,
            showDialog
        ) { up ->
            if (showDialog.value) DialogWithMsg(
                {
                    viewModel.sendCommand(
                        Utils.deviceUUID(),
                        Utils.getControlCharacterValueOptimized("Ctrl-W")
                    )
                    showDialog.value = false
                },
                {
                    showDialog.value = false
                    if (up.content.contains("Invalid Login", true)) viewModel.sendCommand(
                        Utils.deviceUUID(),
                        Utils.getControlCharacterValueOptimized("Ctrl-X")
                    )
                    else viewModel.sendCommand(
                        Utils.deviceUUID(),
                        Utils.getControlCharacterValueOptimized("Ctrl-A")
                    )
                },
                viewModel,
                up,
                showDialog,
                !up.content.contains("Invalid Login", true)
            )
        }
    }

    @Composable
    fun handleResponse(
        password: MutableState<String>,
        email: MutableState<String>,
        envs: List<Dev>,
        selectedEnv: MutableState<String>,
        viewModel: LoginViewModel,
        shellState: ShellUiState,
        cmdState: CommandUiState,
        showDialog: MutableState<Boolean>, onCallBack: @Composable (Popup) -> Unit
    ) {
        if (shellState is ShellUiState.Loading || cmdState is CommandUiState.Loading) LoaderScreen()
        else if (cmdState is CommandUiState.Success) {
            cmdState.response?.let { res ->
                res.formFields.let {
                    if (it.isNullOrEmpty()) {
                        res.popups.let { ups ->
                            if (ups == null || ups.isEmpty()) {
                                val dev = Dev()
                                dev.name = selectedEnv.value
                                val index = envs.indexOf(dev)
                                SharedPref.setLoggedIn(email.value)
                                SharedPref.setLoggedInPwd(password.value)
                                SharedPref.setEnv(Gson().toJson(envs.get(index)))
                                SharedPref.setEnvValue(cmdState.response.env.value)
                                viewModel.fetchUserDetails(
                                    envs.get(index),
                                    cmdState.response.env.value,
                                    email.value,
                                    1,
                                    password.value,
                                    "user/?auth_user_id__username=${email.value}&&values_list=date_format_id__description"
                                )
                                viewModel.fetchUserDetails(
                                    envs.get(index),
                                    cmdState.response.env.value,
                                    email.value,
                                    2,
                                    password.value,
                                    "user/?auth_user_id__username=${email.value}&&values_list=auth_user_id__first_name"
                                )
                                SharedPref.setUserLoggedIn(true)
                                val intent = Intent(this, LandingActivity::class.java)
                                val bundle = Bundle()
                                bundle.putSerializable("response", res)
                                intent.putExtras(bundle)
                                startActivity(intent)
                                finish()
                            } else {
                                showDialog.value = true
                                onCallBack(ups.first())
                            }
                        }
                    } else if (it.toString().contains("Pswd")) {
                        res.popups.let { ups ->
                            if (ups == null || ups.isEmpty()) {
                                showDialog.value = false
                            } else {
                                showDialog.value = true
                                onCallBack(ups.first())
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    @Composable
    fun TermsAndPolicy(checkState: MutableState<Boolean>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    15.dp
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkState.value,
                onCheckedChange = { checkState.value = it },
                modifier = Modifier.padding(),
                enabled = true,

                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.surface
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("I agree to ")
                }
                pushStringAnnotation(tag = "Terms", annotation = "https://www.google.com")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("Terms")
                }

                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append(" and ")
                }

                pushStringAnnotation(tag = "Privacy", annotation = "https://www.google.com")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("Privacy Policy")
                }
            }
            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "Privacy", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val telegram = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        startActivity(telegram)
                    }

                annotatedString.getStringAnnotations(tag = "Terms", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val telegram = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        startActivity(telegram)
                    }
            })

        }
    }

    @Composable
    fun SpinnerSample(
        list: List<Dev>,
        preselected: Dev,
        onSelectionChanged: (myData: Dev) -> Unit,
        modifier: Modifier
    ) {
        var selected by rememberSaveable { mutableStateOf(preselected) }
        var expanded by rememberSaveable { mutableStateOf(true) } // initial value

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onSelectionChanged(selected)
            },
            modifier = modifier  // delete this modifier and use .wrapContentWidth() if you would like to wrap the dropdown menu around the content
        ) {
            list.forEach { listEntry ->
                DropdownMenuItem(
                    onClick = {
                        selected = listEntry
                        expanded = false
                        onSelectionChanged(selected)
                    },
                    content = {
                        Text(
                            text = listEntry.name,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_light)),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth() //optional instad of fillMaxWidth
                        )
                    }
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AppTheme {
            val viewModel = viewModel<LoginViewModel>()
            val modifier = Modifier.fillMaxSize()
            Surface(modifier) {
                Greeting(viewModel, viewModel.shellState, viewModel.cmdState, modifier)
            }
        }
    }
}

@Composable
fun LoaderScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable { }
            .background(
                Brush.linearGradient(
                    listOf(Color.Gray.copy(0.5f), Color.Gray.copy(0.5f))
                )
            )) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}