package com.example.moozik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LessonsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_START_SCREEN, MainActivity.DESTINATION_LESSONS)
        )
        finish()
    }
}
