package com.example.moozik

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class LessonsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons)
        
        // Set lessons icon as active
        MainActivity.setActiveNavIcon(this, 1)
        
        // Bottom Navigation logic
        val btnNavStore = findViewById<LinearLayout>(R.id.btnNavStore)
        val btnNavCart = findViewById<LinearLayout>(R.id.btnNavCart)
        val btnNavProfile = findViewById<LinearLayout>(R.id.btnNavProfile)
        
        btnNavStore.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        btnNavCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
        
        btnNavProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}
