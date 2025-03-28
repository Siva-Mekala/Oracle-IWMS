package com.plcoding.oraclewms.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RemoveCircleOutline
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson
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
                    Greeting(viewModel, viewModel.shellState, viewModel.cmdState, viewModel.addEnv, modifier)
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
        addEnvState: AddEnvState,
        modifier: Modifier
    ) {
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val environment = rememberSaveable { mutableStateOf("dev") }
        var passwordVisible by remember { mutableStateOf(false) }
        val checkState = remember { mutableStateOf(false) }
        var dialog by remember { mutableStateOf(false) }
        val envs by viewModel.envs.collectAsState()
        val context = LocalContext.current
        val envInfo by remember {
            mutableStateOf(EnvInfo())
        }

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
                                    Row {
                                        Icon(
                                            Icons.Outlined.ArrowDropDown,
                                            null,
                                            modifier = Modifier.padding(end = 8.dp).clickable {
                                                checkState.value = true
                                            }
                                        )
                                        Icon(
                                            Icons.Outlined.Add,
                                            null,
                                            modifier = Modifier.padding(end = 8.dp).clickable {
                                                dialog = true
                                            }
                                        )
                                    }
                                },
                                enabled = false,
                                singleLine = true,
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp, 15.dp, 15.dp, 5.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                            if (checkState.value) SpinnerSample(viewModel,
                                envs, envs.first(),
                                onSelectionChanged = {
                                    environment.value = it
                                    checkState.value = false
                                }, Modifier.fillMaxWidth()
                            ) else if (dialog) {
                                    Dialog(onDismissRequest = { dialog = false }) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .padding(16.dp),
                                            shape = RoundedCornerShape(16.dp),
                                        ) {
                                            Text(
                                                text = "Add Environment",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Blue)
                                                    .padding(10.dp),
                                                textAlign = TextAlign.Left,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.padding(15.dp))
                                            EnvironmentRow("name", Icons.Outlined.AccountBox, envInfo)
                                            EnvironmentRow("host", Icons.Outlined.AddHome, envInfo)
                                            EnvironmentRow("port", Icons.Default.PostAdd, envInfo)
                                            EnvironmentRow(
                                                "username",
                                                Icons.Default.VerifiedUser,
                                                envInfo
                                            )
                                            EnvironmentRow("password", Icons.Default.Lock, envInfo)
                                            EnvironmentRow(
                                                "description",
                                                Icons.Default.Description,
                                                envInfo
                                            )
                                            Spacer(modifier = Modifier.padding(15.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(
                                                    onClick = {
                                                        dialog = false
                                                    }
                                                ) {
                                                    Text(
                                                        "Cancel",
                                                        fontSize = 15.sp,
                                                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                                                    )
                                                }
                                                TextButton(
                                                    onClick = {
                                                        //API
                                                        viewModel.addEnvironment(envInfo)
                                                        dialog = false
                                                    }
                                                ) {
                                                    Text(
                                                        "Ok",
                                                        fontSize = 15.sp,
                                                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
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
                                environment.value.apply {
                                    if (isNullOrEmpty()) return@apply
                                    viewModel.startShell(
                                        Utils.deviceUUID(),
                                        environment.value,
                                        email,
                                        password
                                    )
                                }
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
            addEnvState,
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
    fun EnvironmentRow(str: String, vector: ImageVector, envInfo: EnvInfo){
        OutlinedTextField(
            label = {
                Text(
                    str,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                )
            },
            value =  if(str.equals("name")) envInfo.name.value else if (str.equals("host")) envInfo.host.value else if (str.equals("port")) envInfo.port.value else if (str.equals("username")) envInfo.userName.value else if (str.equals("password")) envInfo.password.value else envInfo.description.value,
            singleLine = true,
            leadingIcon = {
                Icon(
                    vector,
                    null,
                    modifier = Modifier.padding(8.dp)
                )
            },
            trailingIcon = null,
            onValueChange = {
                val x = if(str.equals("name")) envInfo.name else if (str.equals("host")) envInfo.host else if (str.equals("port")) envInfo.port else if (str.equals("username")) envInfo.userName else if (str.equals("password")) envInfo.password else envInfo.description
                x.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )
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
        addEnv: AddEnvState,
        showDialog: MutableState<Boolean>, onCallBack: @Composable (Popup) -> Unit
    ) {
        if (shellState is ShellUiState.Loading || cmdState is CommandUiState.Loading || addEnv is AddEnvState.Loading) LoaderScreen()
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

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    @Composable
    fun SpinnerSample(
        viewModel: LoginViewModel,
        list: List<Dev>,
        preselected: Dev,
        onSelectionChanged: (myData: String) -> Unit,
        modifier: Modifier
    ) {
        var selected by rememberSaveable { mutableStateOf(preselected.name) }
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
                        selected = listEntry.name
                        expanded = false
                        onSelectionChanged(selected)
                    },
                    content = {
                        Row {
                            Text(
                                text = listEntry.name,
                                fontFamily = FontFamily(Font(R.font.spacegrotesk_light)),
                                fontSize = 15.sp,
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Outlined.RemoveCircleOutline,
                                null,
                                modifier = Modifier.padding(end = 8.dp).clickable {
                                    viewModel.removeEnvironment(listEntry.name)
                                    selected = ""
                                    expanded = false
                                    onSelectionChanged(selected)
                                }
                            )
                        }
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
                Greeting(viewModel, viewModel.shellState, viewModel.cmdState, viewModel.addEnv, modifier)
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