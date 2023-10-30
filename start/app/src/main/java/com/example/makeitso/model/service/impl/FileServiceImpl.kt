package com.example.makeitso.model.service.impl

import android.net.Uri
import android.util.Log
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.FileService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FileServiceImpl @Inject
constructor(private val storage: FirebaseStorage, private val auth: AccountService) :
    FileService {

    fun uploadPicture(){

    }

    override suspend fun getPlaceholderUri(): Uri{
        Log.d("Hi", storage.getReference("placeholder.jpg").downloadUrl.await().toString())
        return storage.getReference("placeholder.jpg").downloadUrl.await()
    }
}