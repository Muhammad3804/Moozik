package com.example.moozik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moozik.models.CartItem
import com.example.moozik.models.Product

class MainActivity : AppCompatActivity() {

    private var userName: String = DEFAULT_USER_NAME
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)

        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: DEFAULT_USER_NAME

        if (savedInstanceState == null) {
            showStartDestination(intent.getStringExtra(EXTRA_START_SCREEN))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: DEFAULT_USER_NAME
        showStartDestination(intent.getStringExtra(EXTRA_START_SCREEN))
    }

    fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    fun addToCart(product: Product) {
        val existing = cartItems.firstOrNull { it.product.id == product.id }
        if (existing == null) {
            cartItems.add(CartItem(product = product, quantity = 1))
        } else {
            existing.quantity += 1
        }
    }

    fun removeFromCart(productId: String) {
        val existing = cartItems.firstOrNull { it.product.id == productId } ?: return
        if (existing.quantity > 1) {
            existing.quantity -= 1
        } else {
            cartItems.remove(existing)
        }
    }

    fun getCartItems(): List<CartItem> = cartItems.map { it.copy() }

    fun getCartSubtotal(): Int {
        return cartItems.sumOf { parsePrice(it.product.price) * it.quantity }
    }

    private fun parsePrice(value: String): Int {
        return value.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    private fun showStartDestination(destination: String?) {
        when (destination) {
            DESTINATION_LESSONS -> showFragment(LessonsFragment())
            DESTINATION_CART -> showFragment(CartFragment())
            DESTINATION_PROFILE -> showFragment(ProfileFragment())
            DESTINATION_TEACHER -> showFragment(TeacherFragment())
            else -> showFragment(StoreFragment.newInstance(userName))
        }
    }

    @Deprecated("This method has been deprecated in favor of using the OnBackPressedDispatcher")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_START_SCREEN = "extra_start_screen"
        const val EXTRA_USER_NAME = "extra_user_name"
        const val DESTINATION_STORE = "store"
        const val DESTINATION_LESSONS = "lessons"
        const val DESTINATION_CART = "cart"
        const val DESTINATION_PROFILE = "profile"
        const val DESTINATION_TEACHER = "teacher"
        private const val DEFAULT_USER_NAME = "Guest User"
    }
}
