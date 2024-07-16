package com.sap.textrecognition

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
/*    val lencFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }

    val cameraSelector = CameraSelector.Builder().requireLensFacing(lencFacing).build()

    val imageCapture = remember { ImageCapture.Builder().build() }

    val imageAnalyzer = remember { ImageAnalysis.Builder().build() }*/

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit){
        permissionState.launchPermissionRequest()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        val executor = ContextCompat.getMainExecutor(context)
        FloatingActionButton(onClick = { takePicture(executor, cameraController ) }) {
            Text(text = "Camera")
        }
    }) {
        if (permissionState.status.isGranted){

            CameraComposable(modifier = Modifier.padding(it), lifecycleOwner, cameraController)
        }
        else{
            Text(text = "Permission Denied!", modifier = Modifier.padding(it))
        }

    }

}

fun takePicture(
    executor: Executor,
    cameraController: LifecycleCameraController
){
    val file = File.createTempFile("tempFile", ".jpg")
    val outputDir = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(
        outputDir,
        executor,
        object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(savedImage: ImageCapture.OutputFileResults) {
                Log.d("SaveImage", "URI of saved file: ${savedImage.savedUri}")
            }

            override fun onError(savedImageException: ImageCaptureException) {
                Log.d("SaveImage", "Error Saving: ${savedImageException.imageCaptureError}")
            }

        }

    )
}

@Composable
fun CameraComposable(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    cameraController: LifecycleCameraController
) {

    cameraController.bindToLifecycle(lifecycleOwner)
    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView.controller = cameraController

        previewView
    })
}
