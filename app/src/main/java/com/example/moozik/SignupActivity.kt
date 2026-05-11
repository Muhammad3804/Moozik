package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moozik.util.collectEditTexts
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val usernameInput = findViewById<EditText>(R.id.editSignupUsername)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoSignup)
        val editTexts = findViewById<ViewGroup>(android.R.id.content).collectEditTexts()
        val emailInput = editTexts.getOrNull(1)
        val passwordInput = editTexts.getOrNull(2)
        val confirmPasswordInput = editTexts.getOrNull(3)

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnSignup.setOnClickListener {
            val enteredUserName = usernameInput.text?.toString()?.trim().orEmpty()
            val enteredEmail = emailInput?.text?.toString()?.trim().orEmpty()
            val enteredPassword = passwordInput?.text?.toString()?.trim().orEmpty()
            val confirmPassword = confirmPasswordInput?.text?.toString()?.trim().orEmpty()

            if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
                android.widget.Toast.makeText(this, "Email or password cannot be empty", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPasswordInput != null && confirmPassword.isNotBlank() && enteredPassword != confirmPassword) {
                android.widget.Toast.makeText(this, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).await()
                    val userName = enteredUserName.ifBlank { enteredEmail.substringBefore("@").ifBlank { "Guest User" } }
                    withContext(Dispatchers.Main) {
                        startActivity(
                            Intent(this@SignupActivity, MainActivity::class.java)
                                .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                                .putExtra(MainActivity.EXTRA_USER_NAME, userName)
                        )
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(this@SignupActivity, e.message ?: "Signup failed", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
