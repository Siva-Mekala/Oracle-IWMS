package com.plcoding.oraclewms.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.plcoding.oraclewms.api.JSONResponse
import com.plcoding.oraclewms.landing.DetailsScreen
import com.plcoding.oraclewms.login.CommandUiState
import kotlinx.coroutines.launch

class LandingActivity : ComponentActivity() {
    private val TAG = LandingActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//            intent.getSerializableExtra("response", ApiResponse::class.java)
//        else intent.getSerializableExtra("response") as ApiResponse
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
    }

    @Composable
    fun Greeting(modifier: Modifier = Modifier, viewModel: LandingViewModel = viewModel()) {
        val navController = rememberNavController()
        val item = Gson().fromJson(SharedPref.getResponse(), JSONResponse::class.java)
        viewModel.setState(CommandUiState.Success(item))
        DashboardActivityScreen(
            modifier,
            viewModel,
            navController
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
        navController: NavHostController
    ) {
        var showBottomSheet by remember { mutableStateOf(false) }
        var clickPosition by remember { mutableStateOf(0) }
        Scaffold(modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            topBar = {
                DashBoardToolBar(viewModel, modifier)
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
                            clickPosition = it
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
                if (it.value.contains("Ctrl-W") || it.value.contains("Ctrl-D") || it.value.contains(
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
                            if (it.value.contains("Ctrl-X")) {
                                if (navController.currentDestination?.route == "Home") {
                                    viewModel.endShell(
                                        Utils.deviceUUID(),
                                        this@LandingActivity
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
        Column (modifier = Modifier.padding(start = 5.dp, end = 5.dp)){
            var item = viewModel.cmdState
            var info = arrayListOf<HomeInfo>()
            info.add(
                HomeInfo(
                    "Env",
                    if (item is CommandUiState.Success) item.response?.env?.value else ""
                )
            )
            info.add(
                HomeInfo(
                    "Company",
                    if (item is CommandUiState.Success) item.response?.appName?.value else ""
                )
            )
            info.add(
                HomeInfo(
                    "Facility",
                    if (item is CommandUiState.Success) item.response?.facilityName?.value else ""
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
        Row (modifier = Modifier.height(55.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            response?.controls?.let {
                if (it.toString().contains("Ctrl-W"))
                    IconButton(onClick = {
                        if (navController.currentDestination?.route == "Home") {
                            viewModel.endShell(
                                Utils.deviceUUID(),
                                this@LandingActivity
                            )
                        } else {
                            navController.popBackStack()
                            viewModel.sendCommand(
                                Utils.deviceUUID(),
                                Utils.getControlCharacterValueOptimized("Ctrl-W")
                            )
                        }
                    }) {
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
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.double_up_icon),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }
                if (it.toString().contains("Ctrl-D"))
                    IconButton(onClick = {
                        viewModel.sendCommand(
                            Utils.deviceUUID(),
                            Utils.getControlCharacterValueOptimized("Ctrl-D")
                        )
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.double_down_icon),
                            contentDescription = "Localized description",
                            Modifier
                                .size(35.dp)
                                .padding(5.dp)
                        )
                    }
                IconButton(onClick = { onClick() }) {
                    Icon(
                        Icons.Filled.MoreHoriz,
                        contentDescription = "More options",
                        Modifier
                            .size(35.dp)
                            .padding(5.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun DashBoardToolBar(viewModel: LandingViewModel, modifier: Modifier) {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                border = CardDefaults.outlinedCardBorder(true)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Welcome IMWS",
                        Modifier.padding(5.dp),
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_medium))
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
                                viewModel.endShell(Utils.deviceUUID(), this@LandingActivity)
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