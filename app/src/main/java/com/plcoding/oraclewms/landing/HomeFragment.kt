package com.plcoding.focusfun.landing

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(modifier: Modifier, navController: NavController, viewModel: LandingViewModel) {
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

    LazyColumn {
        items(30) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
            ) {
                Text(text = "$it Item", modifier = modifier.padding(15.dp))
            }
        }
    }
}
