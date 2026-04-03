package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoSignup)

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnSignup.setOnClickListener {
            // Navigate to Main Store (MainActivity)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
