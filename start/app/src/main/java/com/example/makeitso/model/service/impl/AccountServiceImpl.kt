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

package com.example.makeitso.model.service.impl

import android.net.Uri
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let {
                        User(
                            id = it.uid,
                            email = it.email,
                            name = it.displayName,
                            authMethod = it.providerData.drop(1).firstOrNull()?.providerId ?: "Unknown",
                            avatarUrl = it.photoUrl
                        )
                    } ?: User()
                    )
                }
            auth.addAuthStateListener(listener)
            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun registerAccount(
        email: String,
        password: String,
    ) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun updateUserProfile(name: String?, avatarUri: Uri?) {
        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().apply {
                name?.let {
                    this.displayName = it
                }
                avatarUri?.let {
                    this.photoUri = it
                }
            }.build()
        ).await()
    }

    override suspend fun updateUserEmail(email: String) {
        auth.currentUser!!.updateEmail(email).await()
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        auth.signInWithCredential(credential).await()
        if (auth.currentUser?.displayName == null) {
            updateUserProfile(name = account.displayName, avatarUri = account.photoUrl)
        }
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()
    }

    companion object {
        private const val LINK_ACCOUNT_TRACE = "linkAccount"
        const val WEB_CLIENT_ID =
            "525981251418-m9bn2bebnm2hiuloddi3hl0f0b652m0o.apps.googleusercontent.com"
    }
}
