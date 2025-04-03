package com.plcoding.oraclewms.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.oraclewms.R
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.home.LandingActivity
import com.plcoding.oraclewms.login.LoginActivity

class SplashActivityView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<SplashViewModel>()
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SplashScreen(viewModel, viewModel.getEnvState) {
                        startActivity(it)
                    }
                }
            }
        }
    }

    private fun startActivity(envApiResponse: ArrayList<Dev>?) {
        if (envApiResponse != null) {
            val gson = Gson()
            SharedPref.setEnvResponse(
                gson.toJson(
                    envApiResponse,
                    object : TypeToken<ArrayList<Dev>>() {}.type
                )
            )
        }
        startActivity(
            Intent(
                this,
                if (SharedPref.isUserLoggedIn()) LandingActivity::class.java
//                else if (!SharedPref.isSeenTc()) TermsAndConditionsView::class.java
                else LoginActivity::class.java
            )
        )
        finish()
    }

    @Composable
    fun SplashScreen(
        viewModel: SplashViewModel,
        envState: EnvironmentsUiState,
        onNavigate: (response: ArrayList<Dev>?) -> Unit = {}
    ) {
        val scale = remember {
            Animatable(1f)
        }
        // AnimationEffect
        LaunchedEffect(key1 = true) {
            viewModel.getEnvironments();
//            scale.animateTo(
//                targetValue = 0.7f,
//                animationSpec = tween(
//                    durationMillis = 2000,
//                    easing = {
//                        OvershootInterpolator(4f).getInterpolation(it)
//                    })
            //   )
        }
        if (envState is EnvironmentsUiState.Success) {
            onNavigate(envState.response)
        } else {
        }


        // Image
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSystemInDarkTheme()) colorResource(R.color.primary_dark_imws) else colorResource(
                        R.color.primary_imws
                    )
                )
        ) {
            Image(
                painter = painterResource(R.drawable.wms_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SplashPreview() {
        val viewModel = viewModel<SplashViewModel>()
        AppTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                SplashScreen(viewModel, viewModel.getEnvState)
            }
        }
    }
}
