// 📄 CameraManager.kt
package com.example.pharmai.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var lifecycleOwner: LifecycleOwner? = null

    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState

    private val _cameraError = MutableStateFlow<String?>(null)
    val cameraError: StateFlow<String?> = _cameraError

    fun setLifecycleOwner(owner: LifecycleOwner) {
        this.lifecycleOwner = owner
    }

    fun startCamera(previewView: PreviewView) {
        val owner = lifecycleOwner
        if (owner == null) {
            _cameraError.value = "Lifecycle owner not set. Camera cannot start."
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                this.cameraProvider = cameraProvider

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        owner, // Use the provided lifecycle owner
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    _cameraError.value = null
                    Log.d("CameraManager", "Camera started successfully")
                } catch (exc: Exception) {
                    Log.e("CameraManager", "Use case binding failed", exc)
                    _cameraError.value = "Failed to start camera: ${exc.message}"
                }
            } catch (exc: Exception) {
                Log.e("CameraManager", "Camera provider future failed", exc)
                _cameraError.value = "Camera initialization failed: ${exc.message}"
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture() {
        val imageCapture = imageCapture ?: run {
            _captureState.value = CaptureState.Error("Camera not ready")
            return
        }

        if (_captureState.value is CaptureState.Capturing) {
            // Already capturing, ignore duplicate request
            return
        }

        _captureState.value = CaptureState.Capturing

        // Create time-stamped output file
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        val file = try {
            File.createTempFile("JPEG_${name}_", ".jpg", storageDir)
        } catch (e: Exception) {
            _captureState.value = CaptureState.Error("Failed to create temp file: ${e.message}")
            return
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val errorMessage = when (exc.imageCaptureError) {
                        ImageCapture.ERROR_CAMERA_CLOSED -> "Camera was closed"
                        ImageCapture.ERROR_INVALID_CAMERA -> "Invalid camera"
                        ImageCapture.ERROR_FILE_IO -> "File I/O error"
                        ImageCapture.ERROR_CAPTURE_FAILED -> "Capture failed"
                        else -> "Photo capture failed: ${exc.message ?: "Unknown error"}"
                    }
                    _captureState.value = CaptureState.Error(errorMessage)
                    Log.e("CameraManager", "Photo capture failed: $errorMessage", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(file)
                    _captureState.value = CaptureState.Success(savedUri)
                    Log.d("CameraManager", "Photo captured successfully: $savedUri")
                }
            }
        )
    }

    fun retryCamera(previewView: PreviewView) {
        _cameraError.value = null
        _captureState.value = CaptureState.Idle
        startCamera(previewView)
    }

    fun cleanup() {
        try {
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
            lifecycleOwner = null
        } catch (e: Exception) {
            Log.e("CameraManager", "Cleanup error", e)
        }
    }
}

sealed class CaptureState {
    object Idle : CaptureState()
    object Capturing : CaptureState()
    data class Success(val imageUri: Uri) : CaptureState()
    data class Error(val message: String) : CaptureState()
}