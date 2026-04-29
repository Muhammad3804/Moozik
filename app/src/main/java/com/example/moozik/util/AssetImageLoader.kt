package com.example.moozik.util

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.moozik.R

/**
 * Small helper to load product images from the assets folder using product title as the filename.
 * Tries a few common filename variants and falls back to a drawable resource if none found.
 */
fun ImageView.loadImageFromAssets(title: String, fallbackRes: Int = R.drawable.ic_image) {
    val ctx: Context = this.context
    val candidates = listOf(
        "$title.jpg",
        "$title.png",
        title.replace(" ", "_") + ".jpg",
        title.replace(" ", "_") + ".png",
        title.lowercase().replace(" ", "_") + ".jpg",
        title.lowercase().replace(" ", "_") + ".png"
    )

    for (name in candidates) {
        try {
            ctx.assets.open(name).use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                // set bitmap on main thread (ImageView methods are main-thread bound; callers are already on main thread)
                this.setImageBitmap(bmp)
                return
            }
        } catch (_: Exception) {
            // try next candidate
        }
    }

    // last resort: set fallback drawable
    try {
        this.setImageResource(fallbackRes)
    } catch (_: Exception) {
        // ignore
    }
}

