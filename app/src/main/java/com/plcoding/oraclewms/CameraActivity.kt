package com.plcoding.oraclewms

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.plcoding.focusfun.landing.LandingViewModel
import com.plcoding.oraclewms.landing.BarCodeActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val modifier = Modifier.fillMaxSize()
                Surface(modifier = modifier) {
                    MyCameraScreen ()
                }
            }
        }
        enableEdgeToEdge()
    }


    @Composable
    fun MyCameraScreen() {
        val context = LocalContext.current
        val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        CameraCapture(
            outputDirectory = outputDirectory,
            onImageCaptured = { uri ->
                Toast.makeText(context, "Image captured: $uri", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("TYPE", uri)
                (context as CameraActivity).setResult(Activity.RESULT_OK, resultIntent)
                context.finish()
            },
            onError = { exc ->
                Toast.makeText(context, "Error capturing image: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @Composable
    fun CameraCapture(
        outputDirectory: File,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val previewView = remember { PreviewView(context) }
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

        Box {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraCapture", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))
            }

            Button (
                onClick = {
                    val photoFile = File(
                        outputDirectory,
                        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                            .format(System.currentTimeMillis()) + ".jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.takePicture(
                        outputOptions, ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                onError(exc)
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                onImageCaptured(savedUri)
                            }
                        }
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text("Capture")
            }
        }
    }
}