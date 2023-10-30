package com.example.makeitso.common.composable

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.makeitso.R

@Composable
fun Avatar(source: Uri?, modifier: Modifier){
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