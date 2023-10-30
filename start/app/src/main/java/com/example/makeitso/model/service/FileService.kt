package com.example.makeitso.model.service

import android.net.Uri

interface FileService {
    suspend fun getPlaceholderUri(): Uri
}