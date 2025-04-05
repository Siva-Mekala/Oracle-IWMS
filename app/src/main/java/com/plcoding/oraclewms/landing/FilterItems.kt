package com.plcoding.oraclewms.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.compose.*
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.landing.FilterUIState.Success
import com.plcoding.oraclewms.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: LoginViewModel,
    onDismiss: (String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        if (viewModel.filterUIState !is Success) viewModel.fetchShipmentNumber(
            SharedPref.getEnvValue(),
            "ib_shipment/?shipment_nbr=${SharedPref.getShipmentID()}&values_list=id",
            true
        )
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss("")
        },
        properties = ModalBottomSheetProperties(
            securePolicy = SecureFlagPolicy.SecureOn,
            isFocusable = true,
            shouldDismissOnBackPress = true
        ),
        sheetState = sheetState,
    ) {
        HandleFilterResponse(viewModel, viewModel.filterUIState, onDismiss)
    }
}

@Composable
fun HandleFilterResponse(viewModel: LoginViewModel, filterUIState: FilterUIState,
                         onDismiss: (String?) -> Unit) {
    if (filterUIState is Success) FilterItems(viewModel, onDismiss)
    else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable { }) {
            if (filterUIState is FilterUIState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else if (filterUIState is FilterUIState.Error){
                Text("Something went wrong", fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light)))
            }
        }
    }
}

@Composable
fun FilterItems(viewModel: LoginViewModel,
                onDismiss: (String?) -> Unit){
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    var text by rememberSaveable { mutableStateOf("") }
    Column (
        modifier = Modifier
            .fillMaxSize(
            )
            .padding(all = 10.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                viewModel.filterText(text)
            },
            label = { Text("Filter Text", fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))) },
            modifier = Modifier.fillMaxWidth()
        )

        if (filteredItems.isEmpty()) {
            Text("No Items found", modifier = Modifier.fillMaxSize().clickable {  }.padding(top = 10.dp, bottom = 10.dp), fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        } else LazyColumn {
            items(
                count = filteredItems.size
            ) {
                Text("${filteredItems[it].item_id__part_a}", modifier = Modifier.fillMaxSize().clickable {
                    onDismiss(filteredItems[it].item_id__part_a)
                }.padding(top = 10.dp, bottom = 10.dp), fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                )
            }
        }
    }
}