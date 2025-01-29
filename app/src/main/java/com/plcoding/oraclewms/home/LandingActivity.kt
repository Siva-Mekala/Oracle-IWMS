package com.plcoding.oraclewms.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plcoding.focusfun.landing.DashBoardScreen
import com.plcoding.focusfun.landing.HomeScreen
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.ui.theme.ComposeTimerTheme
import kotlinx.coroutines.launch

class LandingActivity : ComponentActivity() {
    private val TAG = LandingActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTimerTheme {
                val modifier = Modifier.fillMaxSize()
                val viewModel = viewModel<LandingViewModel>()
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
                ModalDrawerSheet {
                    DrawerContentComponent(
                        closeDrawer = {
                            coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(modifier = modifier
                .statusBarsPadding()
                .navigationBarsPadding(), topBar = {
                DashBoardToolBar(drawerState)
            },
                bottomBar = {
                    bottomAppBar()
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
                                viewModel
                            )
                        }
                        composable(DashBoardScreen.Wallet.route) {
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun DrawerContentComponent(
        closeDrawer: () -> Unit
    ) {
        var info = arrayListOf<HomeInfo>()
        info.add(HomeInfo("Env", "dev"))
        info.add(HomeInfo("Company", "Oracle"))
        info.add(HomeInfo("Facility", "WH1"))
        LazyColumn (verticalArrangement = Arrangement.spacedBy(15.dp), modifier = Modifier.padding(top = 15.dp)){
            items(info.size){
                Row {
                    Text(text = "${info.get(it).header}: ", modifier = Modifier.padding(start = 15.dp))
                    Text(text = info.get(it).subHeader)
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview2() {
        ComposeTimerTheme {
            val modifier = Modifier.fillMaxSize()
            val viewModel = viewModel<LandingViewModel>()
            DashboardActivityScreen(
                modifier,
                viewModel
            )
        }
    }

    @Composable
    fun bottomAppBar(){
        BottomAppBar (
            actions = {
                IconButton (onClick = { /* do something */ }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description")
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Localized description",
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Localized description",
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton (
                    onClick = { /* do something */ },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Filled.MoreVert, "Localized description")
                }
            }
        )
    }

    @Composable
    fun DashBoardToolBar(drawerState: DrawerState) {
        val scope = rememberCoroutineScope()
        TopAppBar(
            backgroundColor = Color.White,
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
                colorFilter = ColorFilter.tint(Color.Black)
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