package com.example.moozik

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLogo = findViewById<ImageView>(R.id.ivSplashLogo)
        try {
            assets.open("splashscreen.png").use { stream ->
                splashLogo.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        } catch (_: Exception) {
            // Keep the existing XML drawable as fallback.
        }

        // Navigate to LoginActivity after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}
