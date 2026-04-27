package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnNoAccount = findViewById<TextView>(R.id.btnNoAccount)
        val emailInput = findViewById<EditText>(R.id.editLoginEmail)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoLogin)

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnLogin.setOnClickListener {
            val enteredEmail = emailInput.text?.toString()?.trim().orEmpty()
            val userName = enteredEmail.substringBefore("@").ifBlank { "Guest User" }

            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                    .putExtra(MainActivity.EXTRA_USER_NAME, userName)
            )
            finish()
        }

        btnNoAccount.setOnClickListener {
            // Navigate to Signup
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
