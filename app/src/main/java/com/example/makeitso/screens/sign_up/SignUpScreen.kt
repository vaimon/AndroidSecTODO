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

package com.example.makeitso.screens.sign_up

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.basicButton
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.R.string as AppText

@Composable
fun SignUpScreen(restartApp: () -> Unit) {
    val fieldModifier = Modifier.fieldModifier()
    val viewModel = hiltViewModel<SignUpViewModel>()
    val uiState = viewModel.uiState.value

    BasicToolbar(AppText.create_account)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(uiState.email, fieldModifier, viewModel::onEmailChange)
        PasswordField(uiState.password, fieldModifier, viewModel::onPasswordChange)
        RepeatPasswordField(uiState.repeatPassword, fieldModifier, viewModel::onRepeatPasswordChange)

        BasicButton(AppText.create_account, Modifier.basicButton()) {
            viewModel.onSignUpClick(restartApp)
        }
    }
}