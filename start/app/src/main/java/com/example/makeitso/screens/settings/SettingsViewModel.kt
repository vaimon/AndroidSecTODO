/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.settings

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.LOGIN_SCREEN
import com.example.makeitso.R
import com.example.makeitso.SIGN_UP_SCREEN
import com.example.makeitso.SPLASH_SCREEN
import com.example.makeitso.common.ext.isValidEmail
import com.example.makeitso.common.snackbar.SnackbarManager
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.FileService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.lastOrNull
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val fileService: FileService,
) : MakeItSoViewModel(logService) {
//  val uiState = accountService.currentUser.map {
//    SettingsUiState(it.isAnonymous)
//  }

    var uiState = mutableStateOf(SettingsUiState())
        private set

    var currentUser = accountService.currentUser

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(SPLASH_SCREEN)
        }
    }

    fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(SPLASH_SCREEN)
        }
    }

    fun onActionClick() {
        if (uiState.value.isEditingMode) {
            val email = uiState.value.emailFieldValue
            val name = uiState.value.nameFieldValue

            if (!email.isValidEmail()) {
                SnackbarManager.showMessage(R.string.email_error)
                return
            }
            launchCatching {
                accountService.updateUserEmail(email)
            }

            launchCatching {
                accountService.updateUserProfile(name = name)
            }
        }
        currentUser = accountService.currentUser
        launchCatching {
            currentUser.collect{
                uiState.value = SettingsUiState(
                    nameFieldValue = it.name ?: "No name",
                    emailFieldValue = it.email ?: "",
                    isEditingMode = !uiState.value.isEditingMode
                )
            }
        }

    }

    fun onNewImageChosen(imgUri: Uri?){
        launchCatching {
            if(imgUri == null)
                return@launchCatching
            val storageImageUri = fileService.uploadImage(imgUri)
            accountService.updateUserProfile(profilePicURI = storageImageUri)
        }
    }

    fun updateNameValue(newValue: String) {
        uiState.value = uiState.value.copy(nameFieldValue = newValue)
    }

    fun updateEmailValue(newValue: String) {
        uiState.value = uiState.value.copy(emailFieldValue = newValue)
    }
}
