package com.example.moozik

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        // Set cart icon as active
        MainActivity.setActiveNavIcon(this, 2)
        
        // Bottom Navigation logic
        val btnNavStore = findViewById<LinearLayout>(R.id.btnNavStore)
        val btnNavLessons = findViewById<LinearLayout>(R.id.btnNavLessons)
        val btnNavProfile = findViewById<LinearLayout>(R.id.btnNavProfile)
        
        btnNavStore.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        btnNavLessons.setOnClickListener {
            startActivity(Intent(this, LessonsActivity::class.java))
            finish()
        }
        
        btnNavProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}
