package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val usernameInput = findViewById<EditText>(R.id.editSignupUsername)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoSignup)

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnSignup.setOnClickListener {
            val userName = usernameInput.text?.toString()?.trim().orEmpty().ifBlank { "Guest User" }

            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_STORE)
                    .putExtra(MainActivity.EXTRA_USER_NAME, userName)
            )
            finish()
        }
    }
}
