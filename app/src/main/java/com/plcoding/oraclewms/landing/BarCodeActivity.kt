package com.plcoding.oraclewms.landing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.plcoding.oraclewms.landing.ui.theme.OracleWMSTheme
import java.util.concurrent.Executors

class BarCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OracleWMSTheme {
                BarcodeScannerScreen()
            }
        }
    }
}

@Composable
fun BarcodeScannerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var barcodeValue by remember { mutableStateOf("") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    fun startCamera(previewView: PreviewView) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(barcodeScanner) { value ->
                        barcodeValue = value

                        val resultIntent = Intent()
                        resultIntent.putExtra("returned_string", barcodeValue)
                        (context as BarCodeActivity).setResult(Activity.RESULT_OK, resultIntent)
                        context.finish()
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    if (hasCameraPermission) {
        AndroidView(
            factory = { PreviewView(it) },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                startCamera(previewView)
            }
        )
        if (barcodeValue.isNotEmpty()) {
            // Display the barcode value (e.g., in a Text composable)
            Log.d("Barcode", "Value: $barcodeValue") // Or display in Text()
            Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()
            barcodeValue = "" // Reset to avoid showing the same value repeatedly
        }
    } else {
        // Request camera permission
        ActivityCompat.requestPermissions(
            context as androidx.activity.ComponentActivity,
            arrayOf(Manifest.permission.CAMERA),
            10 // Request code
        )

        // Handle permission result (in your Activity or a separate function)
        // You'll typically override onRequestPermissionsResult in your Activity
        // and update the hasCameraPermission state accordingly.
    }
}