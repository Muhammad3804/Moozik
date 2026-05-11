package com.example.moozik.util

import android.widget.ImageView
import com.example.moozik.R
import com.example.moozik.models.Product
import com.bumptech.glide.Glide

/**
 * Loads product images from the assets folder using the API-provided imageUrl.
 * Falls back to the given drawable if the asset cannot be loaded.
 */
fun ImageView.loadAssetImage(assetName: String, fallbackRes: Int = R.drawable.ic_image) {
    val source = assetName.trim()
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

fun ImageView.loadProductImage(product: Product, fallbackRes: Int = R.drawable.ic_image) {
    val raw = product.imageUrl.trim()
    val baseName = if (raw.isBlank()) product.name.trim() else raw
    val variants = buildList {
        add(baseName)
        add(product.name.trim())
        add(product.title.trim())
        add(baseName.removeSuffix(".jpg"))
        add(baseName.removeSuffix(".png"))
        add(baseName.replace(" ", "_"))
        add(baseName.replace(" ", "-"))
    }.filter { it.isNotBlank() }.distinct()

    val candidates = buildList {
        variants.forEach { value ->
            add(value)
            add("$value.jpg")
            add("$value.png")
            add("$value.jpeg")
        }
    }

    candidates.forEach { candidate ->
        try {
            if (!candidate.startsWith("http://", ignoreCase = true) &&
                !candidate.startsWith("https://", ignoreCase = true) &&
                !candidate.startsWith("file:", ignoreCase = true)
            ) {
                this.context.assets.open(candidate).close()
            }
            loadAssetImage(candidate, fallbackRes)
            return
        } catch (_: Exception) {
            // try next
        }
    }

    this.setImageResource(fallbackRes)
}
