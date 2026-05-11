package com.example.moozik.data

import com.example.moozik.models.Product
import com.google.gson.annotations.SerializedName

/**
 * Safe DTO for MockAPI response. Supports common field names so the app doesn't crash
 * if the remote schema uses title/image instead of name/imageUrl.
 */
data class ApiProductDto(
    @SerializedName(value = "id") val id: String? = null,
    @SerializedName(value = "name", alternate = ["title"]) val name: String? = null,
    @SerializedName(value = "price") val price: String? = null,
    @SerializedName(value = "category") val category: String? = null,
    @SerializedName(value = "description") val description: String? = null,
    @SerializedName(value = "imageUrl", alternate = ["image"]) val imageUrl: String? = null,
    @SerializedName(value = "rating") val rating: String? = null
) {
    fun toProductOrNull(): Product? {
        val safeName = name?.trim().orEmpty()
        val safeCategory = category?.trim().orEmpty()
        val safeDescription = description?.trim().orEmpty()
        val safePrice = price?.trim().orEmpty()
        val safeImage = imageUrl?.trim().orEmpty()
        val safeRating = rating?.trim().orEmpty()
        val safeId = id?.trim().orEmpty()

        if (safeId.isBlank() || safeName.isBlank() || safeCategory.isBlank()) return null

        return Product(
            id = safeId,
            name = safeName,
            price = safePrice,
            category = safeCategory,
            description = safeDescription,
            imageUrl = if (safeImage.isNotBlank()) safeImage else "$safeName.jpg",
            rating = rating ?: "0.0"
        )
    }
}

