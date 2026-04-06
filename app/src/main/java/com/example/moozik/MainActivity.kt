package com.example.moozik

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

        initializeViews()
        setupNavigationDrawer()
        setupBottomNavigation()
        updateNavigationHeader()

        // Set store icon as active on initial load
        setActiveNavIcon(0)
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btnMenu)
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this)

        // Hamburger menu click handler
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Hide admin panel for now (will show when Firebase is connected)
        val menu = navigationView.menu
        val adminMenuItem = menu.findItem(R.id.nav_admin_panel)
        adminMenuItem.isVisible = false
    }

    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)
        val navHeaderUserName = headerView.findViewById<TextView>(R.id.navHeaderUserName)
        val navHeaderUserEmail = headerView.findViewById<TextView>(R.id.navHeaderUserEmail)

        // Set default user info (will update when Firebase is connected)
        navHeaderUserName.text = "Guest User"
        navHeaderUserEmail.text = "guest@moozik.com"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_admin_panel -> {
                // TODO: Navigate to AdminActivity when implemented
                Toast.makeText(this, "Admin Panel (Coming Soon)", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_guitars -> filterByCategory("Guitars")
            R.id.nav_drums -> filterByCategory("Drums")
            R.id.nav_pianos -> filterByCategory("Pianos")
            R.id.nav_bass -> filterByCategory("Bass")
            R.id.nav_violins -> filterByCategory("Violins")
            R.id.nav_other -> filterByCategory("Other Instruments")
            R.id.nav_logout -> performLogout()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun filterByCategory(category: String) {
        // TODO: Implement category filtering when RecyclerView is added
        Toast.makeText(this, "Filter: $category", Toast.LENGTH_SHORT).show()
    }

    private fun performLogout() {
        // TODO: Implement Firebase logout when connected
        Toast.makeText(this, "Logout (Coming Soon)", Toast.LENGTH_SHORT).show()
        // startActivity(Intent(this, LoginActivity::class.java))
        // finish()
    }

    @Deprecated("This method has been deprecated in favor of using the OnBackPressedDispatcher")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupBottomNavigation() {
        val btnLessons = findViewById<LinearLayout>(R.id.btnNavLessons)
        val btnCart = findViewById<LinearLayout>(R.id.btnNavCart)
        val btnProfile = findViewById<LinearLayout>(R.id.btnNavProfile)

        btnLessons.setOnClickListener {
            startActivity(Intent(this, LessonsActivity::class.java))
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    companion object {
        fun setActiveNavIcon(activity: AppCompatActivity, navIndex: Int) {
            val iconShop = activity.findViewById<ImageView>(R.id.iconShop)
            val iconLessons = activity.findViewById<ImageView>(R.id.iconLessons)
            val iconCart = activity.findViewById<ImageView>(R.id.iconCart)
            val iconProfile = activity.findViewById<ImageView>(R.id.iconProfile)

            // Reset all icons to inactive color
            iconShop?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconLessons?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconCart?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))
            iconProfile?.setColorFilter(android.graphics.Color.parseColor("#FF5252"))

            // Set active icon to active color
            when (navIndex) {
                0 -> iconShop?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                1 -> iconLessons?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                2 -> iconCart?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
                3 -> iconProfile?.setColorFilter(android.graphics.Color.parseColor("#FFC3C3"))
            }
        }
    }

    private fun setActiveNavIcon(navIndex: Int) {
        setActiveNavIcon(this, navIndex)
    }
}
