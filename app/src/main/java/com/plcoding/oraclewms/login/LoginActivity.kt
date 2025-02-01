package com.plcoding.oraclewms.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.Utils
import com.plcoding.oraclewms.home.LandingActivity


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel = viewModel<LoginViewModel>()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Greeting(viewModel, viewModel.shellState, viewModel.cmdState)
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    @Composable
    fun Greeting(viewModel: LoginViewModel, shellState: ShellUiState, cmdState: CommandUiState) {
        var checkState = rememberSaveable { mutableStateOf(false) }
        var email = rememberSaveable { mutableStateOf("") }
        var password = rememberSaveable { mutableStateOf("") }
        var environment by rememberSaveable { mutableStateOf("dev") }
        val envs: ArrayList<String> = Gson().fromJson(SharedPref.getEnvResponse(), object : TypeToken<ArrayList<String?>?>() {}.type)
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text(
                    "iMWS", fontFamily = FontFamily(Font(R.font.jersey_normal)),
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 50.sp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.padding(15.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Column {
                        Text(
                            "Login", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 30.sp
                            ),
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        OutlinedTextField(
                            label = {
                                Text(
                                    "UserName",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                )
                            },
                            value = email.value,
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            },
                            singleLine = true,
                            onValueChange = { email.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp, 15.dp, 15.dp, 5.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                        OutlinedTextField(
                            label = {
                                Text(
                                    "Password",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                                )
                            },
                            value = password.value,
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            },
                            singleLine = true,
                            onValueChange = { password.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
//                        TermsAndPolicy(checkState)
                    }
                }

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SpinnerSample(envs, envs.get(0),
                            onSelectionChanged = {
                                environment = it
                            })
                        Button(
                            onClick = {
                                viewModel.startShell(
                                    Utils.deviceUUID(),
                                    environment,
                                    email,
                                    password
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, bottom = 15.dp, end = 15.dp)
                        ) {
                            Text(
                                "Login", fontFamily = FontFamily(Font(R.font.spacegrotesk_medium)),
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }
        if (shellState is ShellUiState.Loading || cmdState is CommandUiState.Loading) LoaderScreen()
        else if (cmdState is CommandUiState.Success) {
            cmdState.response?.let {
                if (it == null) return@let
                it.popups.let { ups ->
                    if (ups == null || ups.isEmpty()) {
                        SharedPref.setUserLoggedIn(true)
                        val intent = Intent(this, LandingActivity::class.java)
                        var bundle = Bundle()
                        bundle.putSerializable("response", it)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    } else {
                        AlertDialogExample({
                            finish()
                        }, {
                            if (ups[0].content.equals("Invalid Login")) return@AlertDialogExample
                            else viewModel.sendCommand(   Utils.deviceUUID(), "\u0001")
                        }, "Alert", ups[0].content, "Yes", "No")
                    }
                }
            }

        }
    }

    @Composable
    fun TermsAndPolicy(checkState: MutableState<Boolean>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    15.dp
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkState.value,
                onCheckedChange = { checkState.value = it },
                modifier = Modifier.padding(),
                enabled = true,

                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.surface
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("I agree to ")
                }
                pushStringAnnotation(tag = "Terms", annotation = "https://www.google.com")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("Terms")
                }

                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append(" and ")
                }

                pushStringAnnotation(tag = "Privacy", annotation = "https://www.google.com")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.spacegrotesk_light))
                    )
                ) {
                    append("Privacy Policy")
                }
            }
            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "Privacy", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val telegram = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        startActivity(telegram)
                    }

                annotatedString.getStringAnnotations(tag = "Terms", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val telegram = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        startActivity(telegram)
                    }
            })

        }
    }

    @Composable
    fun SpinnerSample(
        list: List<String>,
        preselected: String,
        onSelectionChanged: (myData: String) -> Unit,
        modifier: Modifier = Modifier
            .wrapContentWidth()
            .padding(15.dp)
    ) {
        var selected by rememberSaveable { mutableStateOf(preselected) }
        var expanded by rememberSaveable { mutableStateOf(false) } // initial value

        OutlinedCard(
            modifier = modifier.clickable {
                expanded = !expanded
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selected,
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light)),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.wrapContentWidth()   // delete this modifier and use .wrapContentWidth() if you would like to wrap the dropdown menu around the content
                ) {
                    list.forEach { listEntry ->
                        DropdownMenuItem(
                            onClick = {
                                selected = listEntry
                                expanded = false
                                onSelectionChanged(selected)
                            },
                            content = {
                                Text(
                                    text = listEntry,
                                    fontFamily = FontFamily(Font(R.font.spacegrotesk_light)),
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .wrapContentWidth()  //optional instad of fillMaxWidth
                                )
                            }
                        )
                    }
                }

            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AppTheme {
            val viewModel = viewModel<LoginViewModel>()
            Surface(modifier = Modifier.fillMaxSize()) {
                Greeting(viewModel, viewModel.shellState, viewModel.cmdState)
            }
        }
    }
}

@Composable
fun LoaderScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable { }
            .background(
                Brush.linearGradient(
                    listOf(Color.Gray.copy(0.5f), Color.Gray.copy(0.5f))
                )
            )) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    yes: String,
    no: String
) {
    AlertDialog(
        title = {
            androidx.compose.material.Text(text = dialogTitle)
        },
        text = {
            androidx.compose.material.Text(text = dialogText)
        },
        onDismissRequest = {
            //onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                androidx.compose.material.Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                androidx.compose.material.Text("Dismiss")
            }
        }
    )
}