package com.plcoding.oraclewms.termsAndConditions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.example.compose.AppTheme
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.login.LoginActivity

class TermsAndConditionsView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TermsScreen(onNavigate = this::startActivity, onDecline = this::finish)
                }
            }
        }
    }

    private fun startActivity() {
        SharedPref.setSeenTc(true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @Preview(showBackground = true)
    @Composable
    fun TermsScreen(
        onNavigate: () -> Unit = {},
        onDecline: () -> Unit = {}

    ) {

        val scale = remember {
            Animatable(1f)
        }
        // AnimationEffect
        LaunchedEffect(key1 = true) {
//            viewModel.getEnvironments();

        }
//        if (envState is EnvironmentsUiState.Success) {
//            onNavigate()
//        } else {
//        }


        // Image
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {

            Text(
                modifier = Modifier
                    .scale(scale.value)
                    .padding(top = 15.dp, bottom = 10.dp), text = "Terms And Conditions",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_regular)),
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                ), color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier
                    .scale(scale.value)
                    .padding(25.dp, 10.dp, 25.dp, 10.dp), text = "By accessing this app, we assume you accept these terms and conditions. Do not continue to use, if you do not agree to all of the terms and conditions stated on this page.",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.spacegrotesk_regular)),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ), color = MaterialTheme.colorScheme.secondary
            )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {

                        onDecline()

                    }, modifier = Modifier.padding(10.dp)) {
                        Text("Decline")
                    }
                    Button(onClick = {

                        onNavigate()

                    }, modifier = Modifier.padding(10.dp)  ) {
                        Text("Accept")
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

}