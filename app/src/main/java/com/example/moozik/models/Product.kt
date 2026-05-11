package com.example.moozik.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val rating: String = "0.0"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        price = parcel.readString() ?: "",
        category = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        imageUrl = parcel.readString() ?: "",
        rating = parcel.readString() ?: "0.0"
    )

    val title: String
        get() = name

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeString(rating)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

