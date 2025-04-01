package com.plcoding.oraclewms.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                    LaunchedEffect(true) {
                        viewModel.endShell(Utils.deviceUUID(), this@LoginActivity, "LoginActivity")
                    }
                    LoginScreen(
                        viewModel,
                        viewModel.shellState,
                        viewModel.cmdState,
                        viewModel.addEnv,
                        modifier
                    )
                }
            }
        }
    }

    @Composable
    fun LoginScreen(
        viewModel: LoginViewModel,
        shellState: ShellUiState,
        cmdState: CommandUiState,
        addEnvState: AddEnvState,
        modifier: Modifier
    ) {
        val navController = rememberNavController()
        var title by remember { mutableStateOf("") }
        var dialog by remember { mutableStateOf(false) }
        var showDelete by remember { mutableStateOf<Dev?>(null) }
        val environment = rememberSaveable { mutableStateOf("") }

        Scaffold(
            modifier = modifier
                .statusBarsPadding()
                .navigationBarsPadding(),
            topBar =
            {
                if (title.isNotEmpty()) TopAppBar(title = {
                    Row {
                        Text(
                            "Environments", fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.Add, "add", Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                dialog = true
                                showDelete = null
                            }, tint = Color.Black)
                    }
                }, navigationIcon = {
                    Icon(Icons.Default.ArrowBack, "back", Modifier.clickable {
                        title = ""
                        navController.popBackStack()
                    }, tint = Color.Black)
                }, backgroundColor = Color.White)
            }
        ) { innerPadding ->
            Box(modifier = modifier.padding(innerPadding)) {
                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        Greeting(environment, viewModel, shellState, cmdState, modifier) {
                            title = "Env"
                            navController.navigate("Env")
                        }
                    }
                    composable("Env") {
                        EnvScreen(addEnvState, viewModel, {
                            dialog = true
                            showDelete = it
                        }, {
                            title = ""
                            environment.value = it?.name ?: ""
                            navController.popBackStack()
                        })
                    }
                }
                if (dialog) AddEnvScreen(showDelete, viewModel) {
                    dialog = false
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddEnvScreen(
        showDelete: Dev?,
        viewModel: LoginViewModel,
        onDismiss: () -> Unit
    ) {
        val envInfo by remember {
            mutableStateOf(EnvInfo())
        }
        envInfo.name.value = showDelete?.name ?: ""
        envInfo.host.value = showDelete?.host ?: ""
        envInfo.description.value = showDelete?.description ?: ""

        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            properties = ModalBottomSheetProperties(
                securePolicy = SecureFlagPolicy.SecureOn,
                isFocusable = true,
                shouldDismissOnBackPress = true
            ),
            sheetState = sheetState
        ) {
            Column {
                Text(
                    if (showDelete == null) "Add Environment" else "Environment Details",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_bold)),
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                EnvironmentRow("name", Icons.Outlined.AccountBox, envInfo)
                EnvironmentRow("host", Icons.Outlined.AddHome, envInfo)
                if (showDelete == null) {
                    EnvironmentRow("port", Icons.Default.PostAdd, envInfo)
                    EnvironmentRow(
                        "username",
                        Icons.Default.VerifiedUser,
                        envInfo
                    )
                    EnvironmentRow("password", Icons.Default.Lock, envInfo)
                }
                EnvironmentRow(
                    "description",
                    Icons.Default.Description,
                    envInfo
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)) {
                    if(showDelete != null) Icon(
                        Icons.Default.Delete,
                        "Delete",
                        modifier = Modifier
                            .clickable {
                                coroutineScope
                                    .launch { sheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            if (envInfo.name.value.validate() &&
                                                envInfo.port.value.validate() &&
                                                envInfo.host.value.validate() &&
                                                envInfo.password.value.validate() &&
                                                envInfo.description.value.validate()
                                            ) {
                                                viewModel.removeEnvironment(envInfo.name.value)
                                                onDismiss()
                                            }
                                        }
                                    }
                            }
                            .padding(5.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    if(showDelete == null) Icon(
                        Icons.Default.Done,
                        "Done",
                        modifier = Modifier
                            .clickable {
                                coroutineScope
                                    .launch { sheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            if (envInfo.name.value.validate() &&
                                                envInfo.port.value.validate() &&
                                                envInfo.host.value.validate() &&
                                                envInfo.password.value.validate() &&
                                                envInfo.description.value.validate()
                                            ) {
                                                viewModel.addEnvironment(envInfo)
                                                onDismiss()
                                            }
                                        }
                                    }
                            }
                            .padding(5.dp)
                    )
                }
            }
        }
    }

    private fun String?.validate(): Boolean {
        return !isNullOrEmpty()
    }

    enum class DragValue(val fraction: Float) { Start(0f), End(1f) }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun HorizontalDraggableSample(
        content: @Composable () -> Unit, onDelete: () -> Unit
    ) {
        val density = LocalDensity.current
        val state = remember {
            AnchoredDraggableState(
                initialValue = DragValue.Start,
                positionalThreshold = { distance: Float -> distance * 0.3f },
                velocityThreshold = { with(density) { 50.dp.toPx() } },
                animationSpec = tween(),
            )
        }
        val contentSize = 50.dp
        val contentSizePx = with(density) { contentSize.toPx() }
        Box(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { layoutSize ->
                    val dragEndPoint = contentSizePx
                    state.updateAnchors(
                        DraggableAnchors {
                            DragValue
                                .values()
                                .forEach { anchor ->
                                    anchor at -(dragEndPoint * anchor.fraction)
                                }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterEnd)
                    .background(Color.White)
                    .padding(end = 15.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = {
                            ///////DELETE TASK
                            onDelete()
                        }),
                    tint = Color.Red
                )
            }

            // Main content which moves with swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = state
                                .requireOffset()
                                .roundToInt(),
                            y = 0,
                        )
                    }
                    .anchoredDraggable(state, Orientation.Horizontal)
            ) {
                content()
            }
        }
    }

    @Composable
    fun EnvScreen(
        addEnvState: AddEnvState,
        viewModel: LoginViewModel,
        show: (dev: Dev) -> Unit,
        backPress: (dev: Dev?) -> Unit
    ) {
        BackHandler {
            backPress(null)
        }
        val envoys by viewModel.envs.collectAsState()
        if (envoys.isEmpty()) {
            Text(
                "No Items to display", modifier = Modifier, fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        } else {
            LazyColumn {
                items(envoys.size) { index ->
                    HorizontalDraggableSample({
                        Card(modifier = Modifier
                            .padding(top = 20.dp, start = 15.dp, end = 15.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { show(envoys[index]) }
                                    .padding(10.dp)
                            ) {
                                Text(
                                    envoys[index].name,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.wrapContentWidth(),
                                ) {
                                    Text(
                                        text = envoys[index].host, fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowRight,
                                        contentDescription = "",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }) {
                        viewModel.removeEnvironment(envoys[index].name)
                    }
                }
            }
        }
        if (addEnvState is AddEnvState.Loading) LoaderScreen()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Greeting(
        environment: MutableState<String>,
        viewModel: LoginViewModel,
        shellState: ShellUiState,
        cmdState: CommandUiState,
        modifier: Modifier,
        showEnd: () -> Unit
    ) {
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val checkState = remember { mutableStateOf(false) }
        val permissionState = rememberPermissionState(
            Manifest.permission.CAMERA
        )
        val envoys by viewModel.envs.collectAsState()

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val returnedString =
                    data?.getStringExtra("returned_string") // Get the returned string
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

        Box(
            modifier = modifier.background(
                if (isSystemInDarkTheme()) colorResource(R.color.primary_dark_imws) else colorResource(
                    R.color.primary_imws
                )
            )
        ) {
            Column (verticalArrangement = Arrangement.Center, modifier = Modifier.align(Alignment.Center)) {
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
                                        modifier = Modifier
                                            .padding(end = 8.dp)
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
                            if (checkState.value && envoys.isNotEmpty()) SpinnerSample(viewModel,
                                envoys, envoys.first(),
                                onSelectionChanged = {
                                    environment.value = it
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
                                            if (!checkPermission(
                                                    context,
                                                    Manifest.permission.CAMERA
                                                )
                                            ) {
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
                                                if (!checkPermission(
                                                        context,
                                                        Manifest.permission.CAMERA
                                                    )
                                                ) {
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
            OutlinedButton(onClick = {
                showEnd()
            }, modifier = Modifier.padding(15.dp).align(Alignment.BottomEnd)) {
                Text("Environment", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 15.sp)
                Icon(Icons.Default.Add, "ADD", tint = Color.Black)
            }
        }
        handleResponse(
            password,
            email,
            envoys,
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
    fun EnvironmentRow(str: String, vector: ImageVector, envInfo: EnvInfo) {
        OutlinedTextField(
            label = {
                Text(
                    str,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                )
            },
            value = if (str.equals("name")) envInfo.name.value else if (str.equals("host")) envInfo.host.value else if (str.equals(
                    "port"
                )
            ) envInfo.port.value else if (str.equals("username")) envInfo.userName.value else if (str.equals(
                    "password"
                )
            ) envInfo.password.value else envInfo.description.value,
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
                val x =
                    if (str.equals("name")) envInfo.name else if (str.equals("host")) envInfo.host else if (str.equals(
                            "port"
                        )
                    ) envInfo.port else if (str.equals("username")) envInfo.userName else if (str.equals(
                            "password"
                        )
                    ) envInfo.password else envInfo.description
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
                        Text(
                            text = listEntry.name,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_light)),
                            fontSize = 15.sp,
                            modifier = Modifier
                        )
                    }
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginScreenPreview() {
        val viewModel = viewModel<LoginViewModel>()
        AppTheme {
            LoginScreen(
                viewModel,
                viewModel.shellState,
                viewModel.cmdState,
                viewModel.addEnv,
                Modifier.fillMaxSize()
            )
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