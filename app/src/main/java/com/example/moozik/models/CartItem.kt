package com.example.moozik.models

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

