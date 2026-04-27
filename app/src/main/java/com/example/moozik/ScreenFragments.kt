package com.example.moozik

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

private const val NAV_ACTIVE = "#FFC3C3"
private const val NAV_INACTIVE = "#FF5252"

object BottomNavUi {
    fun apply(root: View, selectedIndex: Int) {
        val iconShop = root.findViewById<ImageView>(R.id.iconShop)
        val iconLessons = root.findViewById<ImageView>(R.id.iconLessons)
        val iconCart = root.findViewById<ImageView>(R.id.iconCart)
        val iconProfile = root.findViewById<ImageView>(R.id.iconProfile)

        iconShop?.setColorFilter(Color.parseColor(NAV_INACTIVE))
        iconLessons?.setColorFilter(Color.parseColor(NAV_INACTIVE))
        iconCart?.setColorFilter(Color.parseColor(NAV_INACTIVE))
        iconProfile?.setColorFilter(Color.parseColor(NAV_INACTIVE))

        when (selectedIndex) {
            0 -> iconShop?.setColorFilter(Color.parseColor(NAV_ACTIVE))
            1 -> iconLessons?.setColorFilter(Color.parseColor(NAV_ACTIVE))
            2 -> iconCart?.setColorFilter(Color.parseColor(NAV_ACTIVE))
            3 -> iconProfile?.setColorFilter(Color.parseColor(NAV_ACTIVE))
        }
    }
}

abstract class BaseScreenFragment(layoutId: Int) : Fragment(layoutId) {
    protected fun navigateTo(fragment: Fragment, addToBackStack: Boolean = false) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    protected fun bindBottomNav(
        root: View,
        selectedIndex: Int,
        onStore: () -> Unit,
        onLessons: () -> Unit,
        onCart: () -> Unit,
        onProfile: () -> Unit,
    ) {
        BottomNavUi.apply(root, selectedIndex)
        root.findViewById<LinearLayout>(R.id.btnNavStore).setOnClickListener { onStore() }
        root.findViewById<LinearLayout>(R.id.btnNavLessons).setOnClickListener { onLessons() }
        root.findViewById<LinearLayout>(R.id.btnNavCart).setOnClickListener { onCart() }
        root.findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener { onProfile() }
    }
}

class StoreFragment : BaseScreenFragment(R.layout.activity_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = arguments?.getString(ARG_USER_NAME) ?: "Guest User"

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)

        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.navHeaderUserName).text = userName
        headerView.findViewById<TextView>(R.id.navHeaderUserEmail).text = "guest@moozik.com"

        navigationView.menu.findItem(R.id.nav_admin_panel)?.isVisible = false

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> navigateTo(ProfileFragment())
                R.id.nav_admin_panel -> Toast.makeText(requireContext(), "Admin Panel (Coming Soon)", Toast.LENGTH_SHORT).show()
                R.id.nav_guitars -> Toast.makeText(requireContext(), "Filter: Guitars", Toast.LENGTH_SHORT).show()
                R.id.nav_drums -> Toast.makeText(requireContext(), "Filter: Drums", Toast.LENGTH_SHORT).show()
                R.id.nav_pianos -> Toast.makeText(requireContext(), "Filter: Pianos", Toast.LENGTH_SHORT).show()
                R.id.nav_bass -> Toast.makeText(requireContext(), "Filter: Bass", Toast.LENGTH_SHORT).show()
                R.id.nav_violins -> Toast.makeText(requireContext(), "Filter: Violins", Toast.LENGTH_SHORT).show()
                R.id.nav_other -> Toast.makeText(requireContext(), "Filter: Other Instruments", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(requireContext(), "Logout (Coming Soon)", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            true
        }

        bindBottomNav(
            root = view,
            selectedIndex = 0,
            onStore = { },
            onLessons = { navigateTo(LessonsFragment()) },
            onCart = { navigateTo(CartFragment()) },
            onProfile = { navigateTo(ProfileFragment()) }
        )
    }

    companion object {
        private const val ARG_USER_NAME = "arg_user_name"

        fun newInstance(userName: String): StoreFragment {
            return StoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_NAME, userName)
                }
            }
        }
    }
}

class LessonsFragment : BaseScreenFragment(R.layout.activity_lessons) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnConnectTeacher).setOnClickListener {
            navigateTo(TeacherFragment(), addToBackStack = true)
        }

        bindBottomNav(
            root = view,
            selectedIndex = 1,
            onStore = {
                navigateTo(
                    StoreFragment.newInstance(
                        requireActivity().intent.getStringExtra(MainActivity.EXTRA_USER_NAME)
                            ?: "Guest User"
                    )
                )
            },
            onLessons = { },
            onCart = { navigateTo(CartFragment()) },
            onProfile = { navigateTo(ProfileFragment()) }
        )
    }
}

class CartFragment : BaseScreenFragment(R.layout.activity_cart) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindBottomNav(
            root = view,
            selectedIndex = 2,
            onStore = {
                navigateTo(
                    StoreFragment.newInstance(
                        requireActivity().intent.getStringExtra(MainActivity.EXTRA_USER_NAME)
                            ?: "Guest User"
                    )
                )
            },
            onLessons = { navigateTo(LessonsFragment()) },
            onCart = { },
            onProfile = { navigateTo(ProfileFragment()) }
        )
    }
}

class ProfileFragment : BaseScreenFragment(R.layout.activity_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindBottomNav(
            root = view,
            selectedIndex = 3,
            onStore = {
                navigateTo(
                    StoreFragment.newInstance(
                        requireActivity().intent.getStringExtra(MainActivity.EXTRA_USER_NAME)
                            ?: "Guest User"
                    )
                )
            },
            onLessons = { navigateTo(LessonsFragment()) },
            onCart = { navigateTo(CartFragment()) },
            onProfile = { }
        )
    }
}
