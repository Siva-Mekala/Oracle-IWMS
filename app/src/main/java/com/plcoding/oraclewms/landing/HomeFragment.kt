package com.plcoding.focusfun.landing

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.api.MenuItem
import com.plcoding.oraclewms.api.Popup
import com.plcoding.oraclewms.landing.DialogWithMsg
import com.plcoding.oraclewms.login.CommandUiState
import com.plcoding.oraclewms.login.LoaderScreen
import java.net.HttpURLConnection

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    state: CommandUiState,
    onItemClick: (MenuItem) -> (Unit)
) {
    Log.d("HomeScreen", "Inside composable")
    viewModel.menuItems.let {
        LazyColumn(
            modifier.background(
                color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                    R.color.white
                )
            )
        ) {
            items(it.size) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isSystemInDarkTheme()) colorResource(R.color.white) else colorResource(
                                R.color.white
                            )
                        )
                        .clickable {
                            onItemClick(it.get(index))
                            navController.navigate("Rewards")
                        }, verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${it.get(index).optionNumber}.",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(5.dp),
                            fontFamily = FontFamily(
                                Font(
                                    R.font.spacegrotesk_medium
                                )
                            ),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${it.get(index).optionName}",
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
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .alpha(0.4f), 2.dp, Color.Gray
                    )
                }
            }
        }
    }
    if (state is CommandUiState.Success) {
        val context = LocalContext.current
        state.response?.let { res ->
            if (res.menuItems.isEmpty()) {
                res.formFields.let {
                    if (it.toString().contains("Pswd")){
                        viewModel.startActivity(context)
                    } else navController.navigate("Rewards")
                }
            } else {
            }
        }
    } else if (state is CommandUiState.Error) {
        val context = LocalContext.current
        if (state.code == HttpURLConnection.HTTP_NOT_FOUND) {
            val showDialog = remember { mutableStateOf(true) }
            DialogWithMsg(
                {},
                onConfirmation = {
                    viewModel.startActivity(context)
                    showDialog.value = false
                },
                viewModel = viewModel,
                ups = Popup("Your session expired. Please login again", "message"),
                showDialog = showDialog,
                false
            )
        } else {
        }
    } else if (state is CommandUiState.Loading) {
        LoaderScreen()
    } else {
    }
}
