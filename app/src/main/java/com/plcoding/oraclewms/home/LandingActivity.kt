package com.plcoding.oraclewms.home

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.google.gson.Gson
import com.plcoding.focusfun.landing.HomeScreen
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.WareHouseApp
import com.plcoding.oraclewms.api.JSONResponse
import com.plcoding.oraclewms.api.NetworkConnectivityObserver
import com.plcoding.oraclewms.landing.DetailsScreen
import com.plcoding.oraclewms.login.CommandUiState
import kotlinx.coroutines.launch

class LandingActivity : ComponentActivity() {
    private val TAG = LandingActivity::class.java.simpleName
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

    fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val modifier = Modifier.fillMaxSize()
                val viewModel = viewModel<LandingViewModel>()
                Surface(modifier = modifier) {
                    Greeting(modifier, viewModel)
                }
            }
        }
        enableEdgeToEdge()
        if (!checkPermission(this, android.Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        NetworkConnectivityObserver.getInstance(this).unregister()
    }


    @Composable
    fun Greeting(modifier: Modifier = Modifier, viewModel: LandingViewModel = viewModel()) {
        NetworkConnectivityObserver.getInstance(this).isConnected.observe(this, { isConnected ->
            viewModel.nwState = isConnected
            if (isConnected) {
                print( "Network is available")
            } else {
                print( "Network is not available")
            }
        })

        val navController = rememberNavController()
        val item = Gson().fromJson(SharedPref.getResponse(), JSONResponse::class.java)
        viewModel.setState(CommandUiState.Success(item))
        DashboardActivityScreen(
            modifier,
            viewModel,
            navController,
            this
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AppTheme {
            val modifier = Modifier.fillMaxSize()
            Surface(modifier = modifier) {
                Greeting(modifier)
            }
        }
    }

    @Composable
    fun DashboardActivityScreen(
        modifier: Modifier = Modifier,
        viewModel: LandingViewModel,
        navController: NavHostController,
        context: Context
    ) {
        var showBottomSheet by remember { mutableStateOf(false) }
        var clickPosition by remember { mutableStateOf(1) }
        Scaffold(modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
            containerColor = if (isSystemInDarkTheme()) colorResource(R.color.secondary_dark_imws) else colorResource(
                R.color.secondary_imws
            ),
            topBar = {
                DashBoardToolBar(viewModel, modifier, context, viewModel.nwState)
            },
            bottomBar = {
                bottomAppBar(
                    viewModel, when (viewModel.cmdState) {
                        is CommandUiState.Success -> (viewModel.cmdState as CommandUiState.Success).response
                        else -> null
                    }, navController
                ) {
                    showBottomSheet = true
                }
            }) { innerPadding ->
            Box(modifier = modifier.padding(innerPadding)) {
                NavHost(
                    navController,
                    startDestination = "Home"
                ) {
                    composable("Home") {
                        HomeScreen(
                            modifier,
                            navController,
                            viewModel,
                            viewModel.cmdState
                        ) {
                            clickPosition = it.option_number
                            SharedPref.setScreenName(it.option_name)
                        }
                    }
                    composable("Rewards") {
                        DetailsScreen(
                            modifier,
                            navController,
                            viewModel,
                            viewModel.cmdState,
                            clickPosition
                        )
                    }
                }
            }
            if (showBottomSheet) MoreInfo(
                viewModel, when (viewModel.cmdState) {
                    is CommandUiState.Success -> (viewModel.cmdState as CommandUiState.Success).response
                    else -> null
                }, navController
            ) {
                showBottomSheet = false
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MoreInfo(
        viewModel: LandingViewModel,
        response: JSONResponse?,
        navController: NavHostController, onDismiss: () -> Unit
    ) {
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            response?.controls?.forEach {
                if (it.value.contains("Ctrl-X") || it.value.contains("Ctrl-D") || it.value.contains(
                        "Ctrl-U"
                    )
                ) return@forEach
                Text(it.value,
                    fontFamily = FontFamily(
                        Font(
                            R.font.spacegrotesk_medium
                        )
                    ),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .clickable {
                            coroutineScope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDismiss()
                                        showBottomSheet = false
                                    }
                                }
                            if (it.value.contains("Ctrl-W")) {
                                if (navController.currentDestination?.route == "Home") {
                                    viewModel.sendCommand(
                                        Utils.deviceUUID(),
                                        Utils.getControlCharacterValueOptimized(
                                            it.value.split(
                                                ":"
                                            )[0]
                                        )
                                    )
                                } else {
                                    viewModel.sendCommand(
                                        Utils.deviceUUID(),
                                        Utils.getControlCharacterValueOptimized(
                                            it.value.split(
                                                ":"
                                            )[0]
                                        )
                                    )
                                    navController.popBackStack()
                                }
                            } else {
                                viewModel.sendCommand(
                                    Utils.deviceUUID(),
                                    Utils.getControlCharacterValueOptimized(it.value.split(":")[0])
                                )
                            }
                        }
                        .padding(5.dp)
                )
            }
        }
    }

    @Composable
    fun DrawerContentComponent(
        viewModel: LandingViewModel
    ) {
        val names: List<String>? = SharedPref.getHomeInfo()?.split(",")
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp)) {
            var info = arrayListOf<HomeInfo>()
            info.add(
                HomeInfo(
                    "Application",
                    names?.get(1)
                )
            )
            info.add(
                HomeInfo(
                    "Env",
                    names?.get(0)
                )
            )

            info.add(
                HomeInfo(
                    "Facility",
                    names?.get(2)
                )
            )
            Row {
                Text(
                    text = "${info.get(0).header}: ",
                    fontFamily = FontFamily(
                        Font(
                            R.font.spacegrotesk_medium
                        )
                    ),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = info.get(0).subHeader ?: "",
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Row {
                Text(
                    text = "${info.get(1).header}: ",
                    fontFamily = FontFamily(
                        Font(
                            R.font.spacegrotesk_medium
                        )
                    ),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = info.get(1).subHeader ?: "",
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Row {
                Text(
                    text = "${info.get(2).header}: ",
                    fontFamily = FontFamily(
                        Font(
                            R.font.spacegrotesk_medium
                        )
                    ),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = info.get(2).subHeader ?: "",
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    @Composable
    fun bottomAppBar(
        viewModel: LandingViewModel,
        response: JSONResponse?,
        navController: NavHostController,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .height(55.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            response?.controls?.let {
                if (it.toString().contains("Ctrl-X"))
                    IconButton(onClick = {
                        if (navController.currentDestination?.route == "Home") {
                            viewModel.sendCommand(
                                Utils.deviceUUID(),
                                Utils.getControlCharacterValueOptimized("Ctrl-X")
                            )
                        } else {
                            navController.popBackStack()
                            viewModel.sendCommand(
                                Utils.deviceUUID(),
                                Utils.getControlCharacterValueOptimized("Ctrl-X")
                            )
                        }
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)

                        )
                    }
                if (it.toString().contains("Ctrl-U"))
                    IconButton(onClick = {
                        viewModel.sendCommand(
                            Utils.deviceUUID(),
                            Utils.getControlCharacterValueOptimized("Ctrl-U")
                        )
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            painter = painterResource(R.drawable.double_up_icon),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }
                else {
                    IconButton(onClick = {
                        viewModel.sendCommand(
                            Utils.deviceUUID(),
                            "\u001bOA"
                        )
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            painter = painterResource(R.drawable.up),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }
                }
                if (it.toString().contains("Ctrl-D"))
                    IconButton(onClick = {
                        viewModel.sendCommand(
                            Utils.deviceUUID(),
                            Utils.getControlCharacterValueOptimized("Ctrl-D")
                        )
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            painter = painterResource(R.drawable.double_down_icon),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }
                else
                    IconButton(onClick = {
                        viewModel.sendCommand(
                            Utils.deviceUUID(),
                            "\u001bOB"
                        )
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            painter = painterResource(R.drawable.down),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }

                IconButton(onClick = { onClick() }, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Filled.MoreHoriz,
                        contentDescription = "More options",
                        Modifier
                            .size(35.dp)
                            .padding(5.dp),
                    )
                }
            }
        }
    }

    @Composable
    fun DashBoardToolBar(
        viewModel: LandingViewModel,
        modifier: Modifier,
        context1: Context,
        nwState: Boolean
    ) {
        val context = LocalContext.current
        val app = context.applicationContext as WareHouseApp
        val name: String by app.userName.observeAsState("")
        TopAppBar(
            backgroundColor = if (isSystemInDarkTheme()) colorResource(R.color.terinary_dark_imws) else colorResource(
                R.color.terinary_imws
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column {
                Card(
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) colorResource(
                            R.color.primary_dark_imws
                        ) else colorResource(R.color.primary_imws)
                    ),
                    border = CardDefaults.outlinedCardBorder(true)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Welcome ${name}",
                            Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = "Logout",
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendCommand(
                                        Utils.deviceUUID(),
                                        Utils.getControlCharacterValueOptimized("Ctrl-W")
                                    )
                                    viewModel.endShell(Utils.deviceUUID(), context1, "logout")
                                }
                                .padding(10.dp)
                                .size(width = 20.dp, height = 20.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                    DrawerContentComponent(
                        viewModel
                    )
                }

                Text(if (nwState) "connected" else "connection lost",
                    Modifier.padding(5.dp),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }

    @Composable
    fun MyEventListener(OnEvent: (event: Lifecycle.Event) -> Unit) {
        val eventHandler = rememberUpdatedState(newValue = OnEvent)
        val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

        DisposableEffect(lifecycleOwner.value) {
            val lifecycle = lifecycleOwner.value.lifecycle
            val observer = LifecycleEventObserver { source, event ->
                eventHandler.value(event)
            }

            lifecycle.addObserver(observer)

            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }

}