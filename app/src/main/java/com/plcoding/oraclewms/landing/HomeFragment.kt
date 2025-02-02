package com.plcoding.focusfun.landing

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    if (state is CommandUiState.Success) {
        state.response?.let { res ->
            if (res.menuItems.isEmpty()) navController.navigate("Rewards")
            else {
                LazyColumn(modifier.background(color = Color.White)) {
                    items(res.menuItems.size) {
                        Column (modifier = Modifier.fillMaxWidth().background(color = Color.White).clickable {
                            onItemClick(res.menuItems.get(it).optionNumber)
                            navController.navigate("Rewards")
                        }, verticalArrangement = Arrangement.Center){
                            Row (modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text = "${res.menuItems.get(it).optionNumber}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.background(color = Color(0xffD3D3D3), shape = CircleShape).padding(5.dp),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.spacegrotesk_medium
                                        )
                                    ),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${res.menuItems.get(it).optionName}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.spacegrotesk_medium
                                        )
                                    ),
                                    fontSize = 15.sp
                                )
                            }
                            HorizontalDivider(Modifier.fillMaxWidth().alpha(0.4f), 2.dp, Color.Gray)
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
