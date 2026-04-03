package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnNoAccount = findViewById<TextView>(R.id.btnNoAccount)
        val logo = findViewById<ImageView>(R.id.ivAuthLogoLogin)

        try {
            assets.open("logo.png").use { stream ->
                logo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep XML fallback drawable.
        }

        btnLogin.setOnClickListener {
            // Navigate to Main Store (MainActivity)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnNoAccount.setOnClickListener {
            // Navigate to Signup
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
