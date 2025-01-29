package com.plcoding.focusfun.landing

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.api.ApiResponse
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    response: ApiResponse?
) {
    LaunchedEffect(Unit) {
        ///viewModel.fetchDashboardData()
    }

    var time by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(true) {
        while (true) {
            time = System.currentTimeMillis()
            delay(1000)
        }
    }
    Log.d("HomeFragment", ""+(response == null))
    response?.let {
        if (it.menuItems.isEmpty()) return@let
        LazyColumn {
            items(response.menuItems.size) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                ) {
                    Text(text = "${response.menuItems.get(it).value}", modifier = modifier.padding(15.dp), fontFamily = FontFamily(
                        Font(
                        R.font.spacegrotesk_medium)
                    ),
                        fontSize = 15.sp)
                }
            }
        }
    }
}
