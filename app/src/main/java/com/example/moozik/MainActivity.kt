package com.example.moozik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private var userName: String = DEFAULT_USER_NAME

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
