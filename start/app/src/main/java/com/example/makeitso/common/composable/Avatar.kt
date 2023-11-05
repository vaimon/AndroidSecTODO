package com.example.makeitso.common.composable

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.makeitso.R
import com.example.makeitso.common.snackbar.SnackbarManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun Avatar(source: Uri?, modifier: Modifier) {
    val painter: Painter = rememberImagePainter(
        data = source ?: R.drawable.placeholder,
        builder = {
            size(300, 300)
            scale(Scale.FILL)
            placeholder(R.drawable.placeholder)
            crossfade(true)
        }
    )
    Image(
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = "Avatar",
        modifier = modifier
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditableAvatar(source: Uri?, modifier: Modifier, onPictureChosen: (Uri?) -> Unit) {
    val launcher = rememberImageChooseLauncher {
        onPictureChosen(it)
    }

    val storagePermissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    Avatar(
        source = source,
        modifier = modifier
            .border(2.0.dp, MaterialTheme.colors.onBackground, CircleShape)
            .clickable {
                       if(storagePermissionState.status.isGranted){
                           launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                       } else{
                           storagePermissionState.launchPermissionRequest()
                       }
            },
    )
}

@Composable
fun rememberImageChooseLauncher(
    onActivityResult: (Uri?) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    return rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onActivityResult(uri)
    }
}