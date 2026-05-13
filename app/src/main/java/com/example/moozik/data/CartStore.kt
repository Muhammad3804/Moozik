package com.example.moozik.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.moozik.models.CartItem
import com.example.moozik.models.Product
import com.google.firebase.auth.FirebaseAuth

private const val DB_NAME = "moozik_store.db"
private const val DB_VERSION = 2

private const val TABLE_PRODUCTS = "products"
private const val TABLE_CART_ITEMS = "cart_items"

private const val COL_PRODUCT_ID = "product_id"
private const val COL_USER_ID = "user_id"
private const val COL_TITLE = "title"
private const val COL_CATEGORY = "category"
private const val COL_PRICE = "price"
private const val COL_RATING = "rating"
private const val COL_DESCRIPTION = "description"
private const val COL_IMAGE_ASSET = "image_asset"
private const val COL_QUANTITY = "quantity"

class MoozikDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS (
                $COL_PRODUCT_ID TEXT PRIMARY KEY,
                $COL_TITLE TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_PRICE TEXT NOT NULL,
                $COL_RATING REAL NOT NULL,
                $COL_DESCRIPTION TEXT NOT NULL,
                $COL_IMAGE_ASSET TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CART_ITEMS (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID TEXT NOT NULL,
                $COL_PRODUCT_ID TEXT NOT NULL,
                $COL_QUANTITY INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY($COL_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COL_PRODUCT_ID) ON DELETE CASCADE,
                UNIQUE($COL_USER_ID, $COL_PRODUCT_ID)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }
}

object CartStore {

    suspend fun addToCart(context: Context, product: Product, quantity: Int = 1) = withContext(Dispatchers.IO) {
        if (quantity <= 0) return@withContext
        val userId = currentUserId() ?: return@withContext
        val db = helper(context).writableDatabase
        db.beginTransaction()
        try {
            upsertProduct(db, product)

            val currentQuantity = getCartQuantity(db, userId, product.id)
            if (currentQuantity == null) {
                val values = ContentValues().apply {
                    put(COL_USER_ID, userId)
                    put(COL_PRODUCT_ID, product.id)
                    put(COL_QUANTITY, quantity)
                }
                db.insert(TABLE_CART_ITEMS, null, values)
            } else {
                updateQuantity(db, userId, product.id, currentQuantity + quantity)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun removeOneFromCart(context: Context, productId: String) = withContext(Dispatchers.IO) {
        val userId = currentUserId() ?: return@withContext
        val db = helper(context).writableDatabase
        db.beginTransaction()
        try {
            val currentQuantity = getCartQuantity(db, userId, productId)
            when {
                currentQuantity == null -> Unit
                currentQuantity > 1 -> updateQuantity(db, userId, productId, currentQuantity - 1)
                else -> db.delete(
                    TABLE_CART_ITEMS,
                    "$COL_USER_ID = ? AND $COL_PRODUCT_ID = ?",
                    arrayOf(userId, productId)
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun setQuantity(context: Context, productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        val userId = currentUserId() ?: return@withContext
        val db = helper(context).writableDatabase
        db.beginTransaction()
        try {
            if (quantity <= 0) {
                db.delete(
                    TABLE_CART_ITEMS,
                    "$COL_USER_ID = ? AND $COL_PRODUCT_ID = ?",
                    arrayOf(userId, productId)
                )
            } else {
                updateQuantity(db, userId, productId, quantity)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun clearCart(context: Context) = withContext(Dispatchers.IO) {
        val userId = currentUserId() ?: return@withContext
        val db = helper(context).writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CART_ITEMS, "$COL_USER_ID = ?", arrayOf(userId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun getCartItems(context: Context, query: String = ""): List<CartItem> = withContext(Dispatchers.IO) {
        val userId = currentUserId() ?: return@withContext emptyList()
        val db = helper(context).writableDatabase
        try {
            val normalized = query.trim()
            val sql = buildString {
                append(
                    """
                    SELECT p.$COL_PRODUCT_ID, p.$COL_TITLE, p.$COL_CATEGORY, p.$COL_PRICE,
                           p.$COL_RATING, p.$COL_DESCRIPTION, p.$COL_IMAGE_ASSET,
                           ci.$COL_QUANTITY
                    FROM $TABLE_CART_ITEMS ci
                    INNER JOIN $TABLE_PRODUCTS p ON p.$COL_PRODUCT_ID = ci.$COL_PRODUCT_ID
                    WHERE ci.$COL_USER_ID = ?
                    """.trimIndent()
                )
                if (normalized.isNotBlank()) {
                    append(" AND (LOWER(p.$COL_TITLE) LIKE ? OR LOWER(p.$COL_CATEGORY) LIKE ?)")
                }
                append(" ORDER BY ci._id DESC")
            }

            val selectionArgs = buildList {
                add(userId)
                if (normalized.isNotBlank()) {
                    val like = "%${normalized.lowercase()}%"
                    add(like)
                    add(like)
                }
            }.toTypedArray()

            val items = mutableListOf<CartItem>()
            db.rawQuery(sql, selectionArgs).use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(COL_PRODUCT_ID)
                val titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE)
                val categoryIndex = cursor.getColumnIndexOrThrow(COL_CATEGORY)
                val priceIndex = cursor.getColumnIndexOrThrow(COL_PRICE)
                val ratingIndex = cursor.getColumnIndexOrThrow(COL_RATING)
                val descriptionIndex = cursor.getColumnIndexOrThrow(COL_DESCRIPTION)
                val quantityIndex = cursor.getColumnIndexOrThrow(COL_QUANTITY)

                while (cursor.moveToNext()) {
                    val product = Product(
                        id = cursor.getString(idIndex) ?: "",
                        name = cursor.getString(titleIndex) ?: "",
                        price = cursor.getString(priceIndex) ?: "",
                        category = cursor.getString(categoryIndex) ?: "",
                        rating = cursor.getString(ratingIndex) ?: "0.0",
                        description = cursor.getString(descriptionIndex) ?: "",
                        imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_ASSET)) ?: ""
                    )
                    items.add(
                        CartItem(
                            product = product,
                            quantity = cursor.getInt(quantityIndex)
                        )
                    )
                }
            }
            items
        } finally {
            db.close()
        }
    }

    suspend fun getSubtotal(context: Context, query: String = ""): Int {
        return getCartItems(context, query).sumOf { parsePrice(it.product.price) * it.quantity }
    }

    private fun helper(context: Context): MoozikDatabaseHelper {
        return MoozikDatabaseHelper(context.applicationContext)
    }

    private fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    private fun upsertProduct(db: SQLiteDatabase, product: Product) {
        val values = ContentValues().apply {
            put(COL_PRODUCT_ID, product.id)
            put(COL_TITLE, product.name)
            put(COL_CATEGORY, product.category)
            put(COL_PRICE, product.price)
            put(COL_RATING, product.rating)
            put(COL_DESCRIPTION, product.description)
            put(COL_IMAGE_ASSET, product.imageUrl)
        }
        db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    private fun getCartQuantity(db: SQLiteDatabase, userId: String, productId: String): Int? {
        db.rawQuery(
            "SELECT $COL_QUANTITY FROM $TABLE_CART_ITEMS WHERE $COL_USER_ID = ? AND $COL_PRODUCT_ID = ?",
            arrayOf(userId, productId)
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else null
        }
    }

    private fun updateQuantity(db: SQLiteDatabase, userId: String, productId: String, quantity: Int) {
        val values = ContentValues().apply {
            put(COL_QUANTITY, quantity)
        }
        db.update(
            TABLE_CART_ITEMS,
            values,
            "$COL_USER_ID = ? AND $COL_PRODUCT_ID = ?",
            arrayOf(userId, productId)
        )
    }

    private fun parsePrice(value: String): Int {
        return value.filter { it.isDigit() }.toIntOrNull() ?: 0
    }
}


