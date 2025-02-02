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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var clickPosition by remember { mutableStateOf(0) }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer) {
                    DrawerContentComponent(
                        closeDrawer = {
                            coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        viewModel
                    )
                }
            }
        ) {
            Scaffold(modifier = modifier
                .statusBarsPadding()
                .navigationBarsPadding(),
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                topBar = {
                    DashBoardToolBar(drawerState, viewModel.cmdState)
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
//                    if (loader is CommandUiState.Loading) LoaderScreen()
                }
                if (showBottomSheet) MoreInfo (viewModel, when (viewModel.cmdState) {
                    is CommandUiState.Success -> (viewModel.cmdState as CommandUiState.Success).response
                    else -> null
                }, navController)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MoreInfo(viewModel: LandingViewModel,
                 response: JSONResponse?,
                 navController: NavHostController) {
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet (
            onDismissRequest = {
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
                    modifier = Modifier.fillMaxWidth().padding(15.dp).clickable {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
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
                }.padding(5.dp))
            }
        }
    }

    @Composable
    fun DrawerContentComponent(
        closeDrawer: () -> Unit,
        viewModel: LandingViewModel
    ) {
        Column {
            var item = viewModel.cmdState
            if (item is CommandUiState.Success) {
                var info = arrayListOf<HomeInfo>()
                info.add(HomeInfo("Env", item.response?.env?.value ?: ""))
                info.add(HomeInfo("Company", item.response?.appName?.value ?: ""))
                info.add(HomeInfo("Facility", item.response?.facilityName?.value ?: ""))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(15.dp)
                ) {
                    items(info.size) {
                        Row {
                            Text(
                                text = "${info.get(it).header}: ",
                                fontFamily = FontFamily(
                                    Font(
                                        R.font.spacegrotesk_medium
                                    )
                                ),
                                fontSize = 15.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = info.get(it).subHeader,
                                fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                                fontSize = 15.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Logout",
                modifier = Modifier
                    .clickable {
                        viewModel.endShell(Utils.deviceUUID(), this@LandingActivity)
                    }
                    .padding(start = 15.dp),
                fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                fontSize = 15.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

    @Composable
    fun bottomAppBar(
        viewModel: LandingViewModel,
        response: JSONResponse?,
        navController: NavHostController,
        onClick : () ->Unit
    ) {
        BottomAppBar(
            actions = {
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
                                Icons.Filled.ArrowBack,
                                contentDescription = "Localized description",
                                Modifier.size(40.dp).padding(5.dp)
                                
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
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Localized description",
                                Modifier.size(40.dp).padding(5.dp)
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
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Localized description",
                                Modifier.size(40.dp).padding(5.dp)
                            )
                        }
                }
            },
            floatingActionButton = {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.MoreHoriz, contentDescription = "More options",   Modifier.size(40.dp).padding(5.dp))
                }
            }
        )
    }

    @Composable
    fun DashBoardToolBar(drawerState: DrawerState, cmdState: CommandUiState) {
        val scope = rememberCoroutineScope()
        TopAppBar(
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            elevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                        .padding(start = 10.dp)
                        .size(width = 48.dp, height = 30.dp),
                    colorFilter = ColorFilter.tint(androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    text = if (cmdState is CommandUiState.Success) {
                        cmdState.response.let { if (it == null) "iMWS" else it.screenName.let { if (it == null) "iMWS" else it.value } }
                    } else "iMWS",
                    modifier = Modifier
                        .padding(start = 15.dp),
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                    fontSize = 20.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
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