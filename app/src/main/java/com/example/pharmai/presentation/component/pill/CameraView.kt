// 📄 CameraView.kt
package com.example.pharmai.presentation.component.pill

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.pharmai.util.CameraManager
import com.example.pharmai.util.CameraUtils
import com.example.pharmai.util.CaptureState

@Composable
fun CameraView(
    onImageCaptured: (ByteArray) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraManager = remember {
        CameraManager(context).apply {
            setLifecycleOwner(lifecycleOwner)
        }
    }

    var showPreview by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val captureState by cameraManager.captureState.collectAsState()
    val cameraError by cameraManager.cameraError.collectAsState()

    // Handle capture state changes
    LaunchedEffect(captureState) {
        when (captureState) {
            is CaptureState.Success -> {
                val uri = (captureState as CaptureState.Success).imageUri
                capturedImageUri = uri
                showPreview = true
            }
            is CaptureState.Error -> {
                // Error is already handled in the state, we just log it here
                val error = (captureState as CaptureState.Error).message
                println("Camera capture error: $error")
            }
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.cleanup()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            cameraError != null -> {
                CameraErrorScreen(
                    errorMessage = cameraError!!,
                    onRetry = {
                        // Create a new PreviewView for retry
                        val previewView = PreviewView(context)
                        cameraManager.retryCamera(previewView)
                    },
                    onClose = onClose
                )
            }
            showPreview && capturedImageUri != null -> {
                ImagePreviewScreen(
                    imageUri = capturedImageUri!!,
                    onRetake = {
                        showPreview = false
                        capturedImageUri = null
                        // Create a new PreviewView for retry
                        val previewView = PreviewView(context)
                        cameraManager.retryCamera(previewView)
                    },
                    onUseImage = { imageData ->
                        onImageCaptured(imageData)
                        onClose()
                    },
                    onClose = onClose
                )
            }
            else -> {
                CameraCaptureScreen(
                    cameraManager = cameraManager,
                    onCapture = { cameraManager.takePicture() },
                    onClose = onClose,
                    isCapturing = captureState is CaptureState.Capturing,
                    captureError = if (captureState is CaptureState.Error) (captureState as CaptureState.Error).message else null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    cameraManager: CameraManager,
    onCapture: () -> Unit,
    onClose: () -> Unit,
    isCapturing: Boolean,
    captureError: String?
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Capture Pill Image",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Camera"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Error Banner - Fixed null handling
                if (!captureError.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = captureError, // This is safe because we checked !isNullOrEmpty()
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Camera Preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { context ->
                            PreviewView(context).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                // Start camera immediately when view is created
                                cameraManager.startCamera(this)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                    }
                }

                // Capture Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onCapture,
                        modifier = Modifier
                            .height(56.dp)
                            .width(200.dp),
                        enabled = !isCapturing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capturing...")
                        } else {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Capture Photo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture")
                        }
                    }
                }

                // Instructions
                Text(
                    text = "Position the pill in the center and ensure good lighting",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    imageUri: Uri,
    onRetake: () -> Unit,
    onUseImage: (ByteArray) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var imageProcessingError by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Preview Image",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Preview"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Error Banner - Fixed null handling
                if (!imageProcessingError.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = imageProcessingError ?: "Unknown error", // Safe with fallback
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Image Preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Captured pill image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.large)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onRetake,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Icon(
                            Icons.Default.Replay,
                            contentDescription = "Retake Photo"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retake")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            imageProcessingError = null
                            isProcessing = true

                            // Convert image to byte array and process
                            val bitmap = CameraUtils.uriToBitmap(context, imageUri)
                            if (bitmap != null) {
                                when (val result = CameraUtils.bitmapToByteArray(bitmap)) {
                                    is CameraUtils.ImageProcessingResult.Success -> {
                                        onUseImage(result.data)
                                    }
                                    is CameraUtils.ImageProcessingResult.Error -> {
                                        imageProcessingError = result.message
                                        isProcessing = false
                                    }
                                }
                            } else {
                                imageProcessingError = "Failed to process image"
                                isProcessing = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Text("Use This Image")
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Camera Error",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Camera Error",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Camera Error",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMessage, // This is non-null because parameter is String (not String?)
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    )
}