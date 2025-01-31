package com.plcoding.oraclewms.home

import android.os.Build
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.plcoding.focusfun.landing.DashBoardScreen
import com.plcoding.focusfun.landing.HomeScreen
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.api.ApiResponse
import com.plcoding.oraclewms.landing.DetailsScreen
import com.plcoding.oraclewms.login.CommandUiState
import kotlinx.coroutines.launch

class LandingActivity : ComponentActivity() {
    private val TAG = LandingActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var items = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("response", ApiResponse::class.java)
        else intent.getSerializableExtra("response") as ApiResponse
        setContent {
            AppTheme {
                val modifier = Modifier.fillMaxSize()
                val viewModel = viewModel<LandingViewModel>()
                viewModel.setState(CommandUiState.Success(items))
                DashboardActivityScreen(
                    modifier,
                    viewModel
                )
            }
        }
        enableEdgeToEdge()
    }

    @Composable
    fun DashboardActivityScreen(
        modifier: Modifier = Modifier,
        viewModel: LandingViewModel
    ) {
        val navController = rememberNavController()

        navController.addOnDestinationChangedListener { controller, destination, args ->
        }
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet (drawerContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer){
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
                .navigationBarsPadding(), containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer, topBar = {
                DashBoardToolBar(drawerState)
            },
                bottomBar = {
                    bottomAppBar(viewModel, when(viewModel.cmdState) { is CommandUiState.Success -> (viewModel.cmdState as CommandUiState.Success).response else -> null })
                }) { innerPadding ->
                Box(modifier = modifier.padding(innerPadding)) {
                    NavHost(
                        navController,
                        startDestination = DashBoardScreen.Home.route
                    ) {
                        composable(DashBoardScreen.Home.route) {
                            HomeScreen(
                                modifier,
                                navController,
                                viewModel,
                                viewModel.cmdState
                            )
                        }
                        composable(DashBoardScreen.Wallet.route) {
                            DetailsScreen(
                                modifier,
                                navController,
                                viewModel,
                                viewModel.cmdState
                            )
                        }
                    }
                }
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
            if(item is CommandUiState.Success) {
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
                                text = "${info.get(it).header}: ", fontFamily = FontFamily(
                                    Font(
                                        R.font.spacegrotesk_medium
                                    )
                                ),
                                fontSize = 15.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
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
            Text(text = "Logout", modifier = Modifier.padding(start = 15.dp), fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                fontSize = 15.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview2() {
        AppTheme  {
            val modifier = Modifier.fillMaxSize()
            val viewModel = viewModel<LandingViewModel>()
            DashboardActivityScreen(
                modifier,
                viewModel
            )
        }
    }

    @Composable
    fun bottomAppBar(viewModel: LandingViewModel, response: ApiResponse?) {
        BottomAppBar (
            actions = {
                response?.controls?.let {
                    if (it.toString().contains("22"))
                        IconButton (onClick = {
                            viewModel.sendCommand("mySessionID123456", "\u0017")
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description")
                        }
                    if (it.toString().contains("19"))
                        IconButton(onClick = {
                            viewModel.sendCommand("mySessionID123456", "\u0015")
                        }) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Localized description",
                            )
                        }
                    if (it.toString().contains("20"))
                        IconButton(onClick = {
                            viewModel.sendCommand("mySessionID123456","\u0004")
                        }) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Localized description",
                        )
                    }
                }
            },
            floatingActionButton = {
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        response?.controls?.forEach {
                            if (it.lineNumber == 20 || it.lineNumber == 19 || it.lineNumber == 22) return@forEach
                            DropdownMenuItem(
                                text = { Text(it.value) },
                                onClick = { /* Do something... */ }
                            )
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun DashBoardToolBar(drawerState: DrawerState) {
        val scope = rememberCoroutineScope()
        TopAppBar(
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            elevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
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