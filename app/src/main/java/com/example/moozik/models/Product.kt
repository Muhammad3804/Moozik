package com.example.moozik.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val price: String,
    val rating: Double,
    val description: String,
    val imageRes: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt().let { if (it == 0) null else it }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(price)
        parcel.writeDouble(rating)
        parcel.writeString(description)
        parcel.writeInt(imageRes ?: 0)
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

