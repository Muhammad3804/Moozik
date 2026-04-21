package com.example.moozik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)

        if (savedInstanceState == null) {
            showStartDestination(intent.getStringExtra(EXTRA_START_SCREEN))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
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

    private fun showStartDestination(destination: String?) {
        when (destination) {
            DESTINATION_LESSONS -> showFragment(LessonsFragment())
            DESTINATION_CART -> showFragment(CartFragment())
            DESTINATION_PROFILE -> showFragment(ProfileFragment())
            DESTINATION_TEACHER -> showFragment(TeacherFragment())
            else -> showFragment(StoreFragment())
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
        const val DESTINATION_STORE = "store"
        const val DESTINATION_LESSONS = "lessons"
        const val DESTINATION_CART = "cart"
        const val DESTINATION_PROFILE = "profile"
        const val DESTINATION_TEACHER = "teacher"
    }
}
