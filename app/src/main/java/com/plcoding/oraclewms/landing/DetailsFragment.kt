package com.plcoding.oraclewms.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.login.CommandUiState

@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    response: CommandUiState,
    clickPosition: Int
) {
    val scanner = GmsBarcodeScanning.getClient(LocalContext.current)
    LaunchedEffect(true) {
        viewModel.sendCommand(
            "mySessionID123456", "${clickPosition}\n"
        )
    }

    if (response is CommandUiState.Success) {
        response.response?.formFields?.let {
            ListScreen(it, scanner)
        }
    }
}

@Composable
fun ListScreen(item: List<FormField>, scanner: GmsBarcodeScanner) {
    LazyColumn (verticalArrangement = Arrangement.spacedBy(15.dp), contentPadding = PaddingValues(start = 15.dp, end = 15.dp)){
        items(item.size) { x ->
            ListItem(item = item.get(x), scanner)
        }
    }
}

@Composable
fun ListItem(item: FormField, scanner: GmsBarcodeScanner) {
    val textObj = rememberSaveable {
        mutableStateOf(
            item.form_value?.trim().let {
                if (it?.contains("<UL>", true) == true) {
                    it.replace("<UL>", "", true)
                } else {
                    it
                }
            }
        )
    }
    val focusRequester = remember { FocusRequester() }
    val cursor = remember { item.cursorState }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        label = {
            Text(
                item?.form_key?:"",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
            )
        },
        value = textObj?.value?:"",
        trailingIcon = {
            Icon(
                Icons.Outlined.Star,
                null,
                modifier = Modifier
                    .clickable {
                        scanner
                            .startScan()
                            .addOnSuccessListener { barcode ->
                                println("barcode")
                                println(barcode.rawValue)
                            }
                            .addOnCanceledListener {
                                println("barcode1")
                            }
                            .addOnFailureListener { e ->
                                println("barcode2")
                            }
                    }
                    .padding(8.dp)
            )
        },
        //enabled = cursor,
        singleLine = true,
        onValueChange = { textObj.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .focusRequester(focusRequester),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

