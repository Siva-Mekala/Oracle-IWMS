package com.plcoding.oraclewms.landing

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
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
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.plcoding.oraclewms.api.FormField

@Composable
fun DetailsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LandingViewModel,
    response: CommandUiState
) {
    val scanner = GmsBarcodeScanning.getClient(LocalContext.current)
    LaunchedEffect(true) {
        viewModel.sendCommand(
            "mySessionID123456","1\n"
        )
    }

    if(response is CommandUiState.Success){
response.response?.formFields?.let {

        ListScreen(it, scanner) {

        }

}

    }
//val items= listOf(
//    FieldData("1", "Item 1"), FieldData("2", "Item 2")
//)
//    ListScreen(items) {
//
//    }
}
@Composable
fun ListScreen(items: List<FormField>, scanner: GmsBarcodeScanner, onItemClick: (String) -> Unit) {
    LazyColumn {
        items(items) { item ->
            ListItem(item = item,scanner, onItemClick = onItemClick)
        }
    }
}

@Composable
fun ListItem(item: FormField, scanner: GmsBarcodeScanner, onItemClick: (String) -> Unit) {
    val textObj = rememberSaveable { mutableStateOf(if(item.formValue.trim().contains("<UL>")) {
        item.formValue.trim().replace("<Ul>","")
    }else{
        item.formValue.trim()
    })  }
val focusRequester= remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.formKey) }
            .padding(16.dp)
    ) {
        OutlinedTextField(
            label = {
                Text(
                    item.formKey,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                )
            },
            value = textObj.value,
            trailingIcon = {
                Icon(
                    Icons.Outlined.Star,
                    null,
                    modifier = Modifier.clickable {

                        scanner.startScan()
                            .addOnSuccessListener { barcode ->
                                println("barcode")
                                println(barcode.rawValue)
                                // Task completed successfully
                            }
                            .addOnCanceledListener {
                                println("barcode1")
                            }

                            .addOnFailureListener { e ->
                                println("barcode2")
                                // Task failed with an exception
                            }
                    }.padding(8.dp)
                )
            },
            readOnly = if(item?.cursor == true){
                false
            }else{
                true
            },

            singleLine = true,
            onValueChange = { textObj.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp ).focusRequester( if (item?.cursor == true) focusRequester else FocusRequester()),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

