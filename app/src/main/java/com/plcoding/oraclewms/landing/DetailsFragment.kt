package com.plcoding.oraclewms.landing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.login.CommandUiState

@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    response: CommandUiState
) {

    Text(text = "Details", modifier = modifier.padding(15.dp), fontFamily = FontFamily(
        Font(
            R.font.spacegrotesk_medium)
    ),
        fontSize = 15.sp)
}