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
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.card
import com.example.makeitso.common.ext.spacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.User

@ExperimentalMaterialApi
@Composable
fun SettingsScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val currentUser = viewModel.currentUser.collectAsStateWithLifecycle(initialValue = User())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.settings,
            modifier = Modifier.toolbarActions(),
            endActionIcon = if (uiState.isEditingMode) AppIcon.ic_check else AppIcon.ic_edit,
            endAction = { viewModel.onActionClick() })

        Spacer(modifier = Modifier.spacer())

        // I'm not sure in this way of props propagation
        if (!uiState.isEditingMode) {
            ProfileInfo(currentUser = currentUser)
        }else{
            EditableProfileInfo(currentUser = currentUser, viewModel)
        }

        Spacer(modifier = Modifier.spacer())

        SignOutCard { viewModel.onSignOutClick(restartApp) }
        DeleteMyAccountCard { viewModel.onDeleteMyAccountClick(restartApp) }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ProfileInfo(currentUser: State<User>) {
    Avatar(source = currentUser.value.avatarUrl,
        Modifier
            .width(128.0.dp)
            .height(128.0.dp)
            .clip(CircleShape)
    )
    Spacer(modifier = Modifier.spacer())
    Text(
        text = currentUser.value.name ?: "No name",
        fontSize = 24.0.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = currentUser.value.email ?: "No email",
        fontSize = 20.0.sp,
    )
    Text(text = stringResource(R.string.auth_type, currentUser.value.authMethod))
}

@ExperimentalMaterialApi
@Composable
private fun EditableProfileInfo(currentUser: State<User>, viewModel: SettingsViewModel) {
    EditableAvatar(source = currentUser.value.avatarUrl,
        Modifier
            .width(128.0.dp)
            .height(128.0.dp)
            .clip(CircleShape)
    ){
        viewModel.onNewImageChosen(it)
    }
    Spacer(modifier = Modifier.spacer())
    InputField(
        value = viewModel.uiState.value.nameFieldValue,
        onNewValue = { newValue -> viewModel.updateNameValue(newValue)},
    )
    Spacer(modifier = Modifier.spacer())
    if(!currentUser.value.isAuthenticatedWithProvider()){
        InputField(
            value = viewModel.uiState.value.emailFieldValue,
            onNewValue = { newValue -> viewModel.updateEmailValue(newValue)},
        )
    }
}

@Composable
fun InputField(value: String, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
    )
}

@ExperimentalMaterialApi
@Composable
private fun SignOutCard(signOut: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    RegularCardEditor(AppText.sign_out, AppIcon.ic_exit, "", Modifier.card()) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(AppText.sign_out_title)) },
            text = { Text(stringResource(AppText.sign_out_description)) },
            dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(AppText.sign_out) {
                    signOut()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun DeleteMyAccountCard(deleteMyAccount: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    DangerousCardEditor(
        AppText.delete_my_account,
        AppIcon.ic_delete_my_account,
        "",
        Modifier.card()
    ) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(AppText.delete_account_title)) },
            text = { Text(stringResource(AppText.delete_account_description)) },
            dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(AppText.delete_my_account) {
                    deleteMyAccount()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}
