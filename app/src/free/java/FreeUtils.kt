import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.WareHouseApp
import com.plcoding.oraclewms.home.HomeInfo

@Composable
fun FreeText(testSize: Int, imageSize: Int) {
    Text(
        "XPress WMS",
        fontSize = testSize.sp,
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.spacegrotesk_bold))
    )
}

@Composable
fun DashBoardToolBar(
    viewModel: LandingViewModel,
    context1: Context,
) {
    val context = LocalContext.current
    val app = context.applicationContext as WareHouseApp
    val name: String by app.userName.observeAsState("")
    var state: Boolean by rememberSaveable { mutableStateOf(false) }
    androidx.compose.material.TopAppBar(
        backgroundColor = if (isSystemInDarkTheme()) colorResource(R.color.terinary_dark_imws) else colorResource(
            R.color.terinary_imws
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (state) 120.dp else 150.dp)
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
                DrawerContentComponent()
            }
            InternetConnectivityChanges { xyz, _ ->
                state = xyz
            }

            if (!state) Text(
                "connection lost",
                Modifier.padding(5.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_regular)),
                color = Color.Red
            )
        }
    }
}

@Composable
fun DrawerContentComponent() {
    val names: List<String>? = SharedPref.getHomeInfo()?.split(",")
    Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp)) {
        val info = arrayListOf<HomeInfo>()
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
fun InternetConnectivityChanges(
    onStateChange: (state: Boolean, network: Network) -> Unit
) {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCallback = remember {
        object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                println("available")
                onStateChange(true, network)
            }

            override fun onLost(network: Network) {
                println("onLost")
                onStateChange(false, network)
            }
        }
    }

    LaunchedEffect(key1 = context) {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}