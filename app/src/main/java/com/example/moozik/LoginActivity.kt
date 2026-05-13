package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moozik.util.collectEditTexts
import com.example.moozik.util.findImageViewByContentDescription
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val googleSignInClient: GoogleSignInClient by lazy {
        val webClientId = getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth.currentUser?.let { currentUser ->
            if (currentUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } || currentUser.isEmailVerified) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                        .putExtra(MainActivity.EXTRA_USER_NAME, currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Guest User")
                )
                finish()
                return
            } else {
                auth.signOut()
            }
        }

        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnNoAccount = findViewById<TextView>(R.id.btnNoAccount)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoLogin)
        val contentRoot = findViewById<ViewGroup>(android.R.id.content)
        val editTexts = contentRoot.collectEditTexts()
        val emailInput = findViewById<EditText>(R.id.editLoginEmail)
        val passwordInput = editTexts.getOrNull(1)
        val googleButton = contentRoot.findImageViewByContentDescription("Google")

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnLogin.setOnClickListener {
            val enteredEmail = emailInput.text?.toString()?.trim().orEmpty()
            val enteredPassword = passwordInput?.text?.toString()?.trim().orEmpty()

            if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
                android.widget.Toast.makeText(this, "Email or password cannot be empty", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    auth.signInWithEmailAndPassword(enteredEmail, enteredPassword).await()
                    val firebaseUser = auth.currentUser
                    if (firebaseUser == null) {
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(this@LoginActivity, "Login failed", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    if (canEnterApp(firebaseUser)) {
                        val userName = firebaseUser.displayName
                            ?: enteredEmail.substringBefore("@").ifBlank { "Guest User" }
                        withContext(Dispatchers.Main) {
                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                                    .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                                    .putExtra(MainActivity.EXTRA_USER_NAME, userName)
                            )
                            finish()
                        }
                    } else {
                        firebaseUser.sendEmailVerification()
                            .addOnSuccessListener {
                                android.widget.Toast.makeText(this@LoginActivity, "Please verify your email first. Check your inbox.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                android.widget.Toast.makeText(this@LoginActivity, "Please verify your email first. Check your inbox.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .addOnCompleteListener {
                                auth.signOut()
                            }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(this@LoginActivity, e.message ?: "Login failed", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnNoAccount.setOnClickListener {
            // Navigate to Signup
            startActivity(Intent(this, SignupActivity::class.java))
        }

        googleButton?.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
        }
        (googleButton?.parent as? View)?.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RC_GOOGLE_SIGN_IN) return

        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (account == null) {
                android.widget.Toast.makeText(this, "Google sign-in failed", android.widget.Toast.LENGTH_SHORT).show()
                return
            }

            handleGoogleAccount(account)
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, e.message ?: "Google sign-in failed", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleGoogleAccount(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if (idToken.isNullOrBlank()) {
            android.widget.Toast.makeText(this, "Google account missing token", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                val userName = auth.currentUser?.displayName
                    ?: auth.currentUser?.email?.substringBefore("@")
                    ?: account.email?.substringBefore("@")
                    ?: "Guest User"
                withContext(Dispatchers.Main) {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                            .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                            .putExtra(MainActivity.EXTRA_USER_NAME, userName)
                    )
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(this@LoginActivity, e.message ?: "Google sign-in failed", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun canEnterApp(user: FirebaseUser): Boolean {
        user.reload().await()
        return user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } || user.isEmailVerified
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
    }
}
