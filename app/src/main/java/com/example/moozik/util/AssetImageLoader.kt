package com.example.moozik.util

import android.widget.ImageView
import com.example.moozik.R
import com.example.moozik.models.Product
import com.bumptech.glide.Glide

/**
 * Loads product images from the assets folder using the API-provided imageUrl.
 * Falls back to the given drawable if the asset cannot be loaded.
 */
fun ImageView.loadProductImage(product: Product, fallbackRes: Int = R.drawable.ic_image) {
    if (product.imageUrl.isBlank()) {
        this.setImageResource(fallbackRes)
        return
    }
    val source = product.imageUrl.trim().ifBlank { "${product.name}.jpg" }
    val model = when {
        source.startsWith("http://", ignoreCase = true) || source.startsWith("https://", ignoreCase = true) -> source
        source.startsWith("file:", ignoreCase = true) -> source
        else -> "file:///android_asset/$source"
    }
    Glide.with(this)
        .load(model)
        .placeholder(fallbackRes)
        .error(fallbackRes)
        .into(this)
}