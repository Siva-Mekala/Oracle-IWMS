package com.plcoding.oraclewms.splash

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.login.LoginActivity
import com.plcoding.oraclewms.ui.theme.ComposeTimerTheme
import kotlinx.coroutines.delay

class SplashActivityView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTimerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SplashScreen(onNavigate = this::startActivity)
                }
            }
        }
    }

    private fun startActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
            Toast.makeText(
                this,
                "Please provide notification permission to get started",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    @Composable
    fun SplashScreen(onNavigate: () -> Unit = {}) {
        val scale = remember {
            Animatable(0f)
        }
        // AnimationEffect
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    })
            )
            delay(2000L)
            onNavigate()
        }

        // Image
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF531CB3), Color(0xFF8200C7))
                    )
                )
        ) {
            Text(
                modifier = Modifier
                    .scale(scale.value)
                    .padding(top = 15.dp, bottom = 10.dp), text = "iWMS",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.jersey_normal)),
                    fontSize = 50.sp,
                    lineHeight = 50.sp
                ), color = Color.White
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SplashPreview() {
        ComposeTimerTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                SplashScreen()
            }
        }
    }
}
