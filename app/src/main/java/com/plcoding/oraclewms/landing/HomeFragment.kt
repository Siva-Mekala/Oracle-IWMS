package com.plcoding.focusfun.landing

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.login.CommandUiState
import com.plcoding.oraclewms.login.LoaderScreen
import java.net.HttpURLConnection

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    state: CommandUiState,
    onItemClick: (Int) -> (Unit)
) {
    Log.d("HomeScreen", "Inside composable")

    LaunchedEffect(Unit) {
    }

    if (state is CommandUiState.Success) {
        state.response?.let { res ->
            if (res.menuItems.isEmpty()) navController.navigate("Rewards")
            else {
                LazyColumn(modifier) {
                    items(res.menuItems.size) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                                .clickable {
                                    onItemClick(res.menuItems.get(it).optionNumber)
                                    navController.navigate("Rewards")
                                },
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = "${res.menuItems.get(it).value}",
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = modifier.padding(15.dp),
                                fontFamily = FontFamily(
                                    Font(
                                        R.font.spacegrotesk_medium
                                    )
                                ),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    } else if (state is CommandUiState.Error) {
        if (state.code == HttpURLConnection.HTTP_NOT_FOUND) viewModel.startActivity(LocalContext.current)
        else {
        }
    } else if (state is CommandUiState.Loading){
        LoaderScreen()
    } else {

    }
}
