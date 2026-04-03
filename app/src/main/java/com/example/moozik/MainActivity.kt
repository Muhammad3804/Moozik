package com.example.moozik

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // We apply 0 padding because we want the header and footer to reach the screen edges.
            // Internal padding in activity_main.xml handles the spacing for content.
            v.setPadding(0, 0, 0, 0)
            insets
        }

        // Bottom Navigation logic
        val btnLessons = findViewById<LinearLayout>(R.id.btnNavLessons)
        val btnCart = findViewById<LinearLayout>(R.id.btnNavCart)
        val btnProfile = findViewById<LinearLayout>(R.id.btnNavProfile)

        btnLessons.setOnClickListener {
            startActivity(Intent(this, LessonsActivity::class.java))
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        // Set store icon as active on initial load
        setActiveNavIcon(0)
    }

    companion object {
        fun setActiveNavIcon(activity: AppCompatActivity, navIndex: Int) {
            val iconShop = activity.findViewById<ImageView>(R.id.iconShop)
            val iconLessons = activity.findViewById<ImageView>(R.id.iconLessons)
            val iconCart = activity.findViewById<ImageView>(R.id.iconCart)
            val iconProfile = activity.findViewById<ImageView>(R.id.iconProfile)

            // Reset all icons to inactive color
            iconShop?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconLessons?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconCart?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconProfile?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))

            // Set active icon to active color
            when (navIndex) {
                0 -> iconShop?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                1 -> iconLessons?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                2 -> iconCart?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                3 -> iconProfile?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
            }
        }
    }

    private fun setActiveNavIcon(navIndex: Int) {
        setActiveNavIcon(this, navIndex)
    }
}
