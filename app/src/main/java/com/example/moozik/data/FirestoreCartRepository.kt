package com.example.moozik.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val CART_COLLECTION = "cart_items"

data class FirestoreCartItem(
    val cartItemId: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val price: String,
    val quantity: Int,
    val imageUrl: String = ""
)

class FirestoreCartRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun addToCart(
        userId: String,
        productId: String,
        productName: String,
        price: String,
        quantity: Int,
        imageUrl: String = ""
    ) = withContext(Dispatchers.IO) {
        val existing = getCartItems(userId).firstOrNull { it.productId == productId }
        if (existing == null) {
            val document = hashMapOf(
                "userId" to userId,
                "productId" to productId,
                "productName" to productName,
                "price" to price,
                "quantity" to quantity,
                "imageUrl" to imageUrl
            )
            db.collection(CART_COLLECTION).add(document).await()
        } else {
            updateQuantity(existing.cartItemId, existing.quantity + quantity)
        }
    }

    suspend fun getCartItems(userId: String): List<FirestoreCartItem> = withContext(Dispatchers.IO) {
        val snapshot = db.collection(CART_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        snapshot.documents.mapNotNull { document ->
            val productId = document.getString("productId") ?: return@mapNotNull null
            val productName = document.getString("productName") ?: return@mapNotNull null
            val price = document.getString("price") ?: ""
            val quantity = document.getLong("quantity")?.toInt() ?: 1
            FirestoreCartItem(
                cartItemId = document.id,
                userId = userId,
                productId = productId,
                productName = productName,
                price = price,
                quantity = quantity,
                imageUrl = document.getString("imageUrl").orEmpty()
            )
        }
    }

    suspend fun updateQuantity(cartItemId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        db.collection(CART_COLLECTION)
            .document(cartItemId)
            .update("quantity", newQuantity)
            .await()
    }

    suspend fun removeFromCart(cartItemId: String) = withContext(Dispatchers.IO) {
        db.collection(CART_COLLECTION)
            .document(cartItemId)
            .delete()
            .await()
    }

    fun listenToCart(userId: String, onUpdate: (List<FirestoreCartItem>) -> Unit): ListenerRegistration {
        return db.collection(CART_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val items = snapshot.documents.mapNotNull { document ->
                    val productId = document.getString("productId") ?: return@mapNotNull null
                    val productName = document.getString("productName") ?: return@mapNotNull null
                    val price = document.getString("price") ?: ""
                    val quantity = document.getLong("quantity")?.toInt() ?: 1
                    FirestoreCartItem(
                        cartItemId = document.id,
                        userId = userId,
                        productId = productId,
                        productName = productName,
                        price = price,
                        quantity = quantity,
                        imageUrl = document.getString("imageUrl").orEmpty()
                    )
                }
                onUpdate(items)
            }
    }

    suspend fun findCartItemId(userId: String, productId: String): String? = withContext(Dispatchers.IO) {
        getCartItems(userId).firstOrNull { it.productId == productId }?.cartItemId
    }

    suspend fun updateQuantityForProduct(userId: String, productId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        val cartItemId = findCartItemId(userId, productId) ?: return@withContext
        updateQuantity(cartItemId, newQuantity)
    }

    suspend fun removeByProductId(userId: String, productId: String) = withContext(Dispatchers.IO) {
        val cartItem = getCartItems(userId).firstOrNull { it.productId == productId } ?: return@withContext
        if (cartItem.quantity > 1) {
            updateQuantity(cartItem.cartItemId, cartItem.quantity - 1)
        } else {
            removeFromCart(cartItem.cartItemId)
        }
    }
}


