package com.example.moozik

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Set profile icon as active
        MainActivity.setActiveNavIcon(this, 3)
        
        // Bottom Navigation logic
        val btnNavStore = findViewById<LinearLayout>(R.id.btnNavStore)
        val btnNavLessons = findViewById<LinearLayout>(R.id.btnNavLessons)
        val btnNavCart = findViewById<LinearLayout>(R.id.btnNavCart)
        
        btnNavStore.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        btnNavLessons.setOnClickListener {
            startActivity(Intent(this, LessonsActivity::class.java))
            finish()
        }
        
        btnNavCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
    }
}
