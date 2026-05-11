package com.example.moozik

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.core.graphics.toColorInt
import com.example.moozik.data.CartStore
import com.example.moozik.adapters.CartAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView

private const val NAV_ACTIVE = "#FFC3C3"
private const val NAV_INACTIVE = "#FF5252"

object BottomNavUi {
    fun apply(root: View, selectedIndex: Int) {
        val iconShop = root.findViewById<ImageView>(R.id.iconShop)
        val iconLessons = root.findViewById<ImageView>(R.id.iconLessons)
        val iconCart = root.findViewById<ImageView>(R.id.iconCart)
        val iconProfile = root.findViewById<ImageView>(R.id.iconProfile)

        iconShop?.setColorFilter(NAV_INACTIVE.toColorInt())
        iconLessons?.setColorFilter(NAV_INACTIVE.toColorInt())
        iconCart?.setColorFilter(NAV_INACTIVE.toColorInt())
        iconProfile?.setColorFilter(NAV_INACTIVE.toColorInt())

        when (selectedIndex) {
            0 -> iconShop?.setColorFilter(NAV_ACTIVE.toColorInt())
            1 -> iconLessons?.setColorFilter(NAV_ACTIVE.toColorInt())
            2 -> iconCart?.setColorFilter(NAV_ACTIVE.toColorInt())
            3 -> iconProfile?.setColorFilter(NAV_ACTIVE.toColorInt())
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

    private val sectionIds = mapOf(
        "Guitar" to R.id.sectionGuitars,
        "Drums" to R.id.sectionDrums,
        "Piano" to R.id.sectionPianos,
        "Bass" to R.id.sectionBass,
        "Violin" to R.id.sectionViolins,
        "Other" to R.id.sectionOthers,
    )

    private var currentSearchQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = arguments?.getString(ARG_USER_NAME) ?: "Guest User"

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)

        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.navHeaderUserName).text = userName
        headerView.findViewById<TextView>(R.id.navHeaderUserEmail).text = getString(R.string.guest_email)

        navigationView.menu.findItem(R.id.nav_admin_panel)?.isVisible = false

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> navigateTo(ProfileFragment())
                R.id.nav_admin_panel -> Toast.makeText(requireContext(), "Admin Panel (Coming Soon)", Toast.LENGTH_SHORT).show()
                R.id.nav_guitars -> showOnlySection(view, "Guitar")
                R.id.nav_drums -> showOnlySection(view, "Drums")
                R.id.nav_pianos -> showOnlySection(view, "Piano")
                R.id.nav_bass -> showOnlySection(view, "Bass")
                R.id.nav_violins -> showOnlySection(view, "Violin")
                R.id.nav_other -> showOnlySection(view, "Other")
                R.id.nav_logout -> logoutToLogin()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
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

        CartStore.syncCatalog(requireContext())

        view.findViewById<EditText>(R.id.searchStoreBar)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s?.toString().orEmpty().trim()
                renderStoreSections(view)
            }
        })

        renderStoreSections(view)
    }

    private fun showAllSections(root: View) {
        sectionIds.values.forEach { id -> root.findViewById<View>(id)?.visibility = View.VISIBLE }
    }

    private fun showOnlySection(root: View, category: String) {
        sectionIds.forEach { (mappedCategory, sectionId) ->
            root.findViewById<View>(sectionId)?.visibility = if (mappedCategory == category) View.VISIBLE else View.GONE
        }
    }

    private fun renderStoreSections(root: View) {
        sectionIds.forEach { (category, sectionId) ->
            val recyclerId = when (category) {
                "Guitar" -> R.id.recyclerGuitars
                "Drums" -> R.id.recyclerDrums
                "Piano" -> R.id.recyclerPianos
                "Bass" -> R.id.recyclerBass
                "Violin" -> R.id.recyclerViolins
                else -> R.id.recyclerOthers
            }
            setupProductSection(root, recyclerId, category, currentSearchQuery)
            val filtered = com.example.moozik.data.ProductRepository.allProducts().filter {
                it.category == category && matchesSearch(it, currentSearchQuery)
            }
            root.findViewById<View>(sectionId)?.visibility = if (currentSearchQuery.isBlank() || filtered.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun logoutToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupProductSection(root: View, recyclerId: Int, category: String, query: String = "") {
        val recycler = root.findViewById<RecyclerView>(recyclerId)
        val products = com.example.moozik.data.ProductRepository.allProducts().filter {
            it.category == category && matchesSearch(it, query)
        }
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.adapter = com.example.moozik.adapters.ProductAdapter(products) { product ->
            navigateTo(ProductFragments.ProductDetailFragment.newInstance(product), addToBackStack = true)
        }

        if (recycler.itemDecorationCount == 0) {
            val spacing = resources.getDimensionPixelSize(R.dimen.catalog_card_spacing)
            recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.left = spacing / 2
                    outRect.right = spacing / 2
                    outRect.top = spacing / 2
                    outRect.bottom = spacing / 2
                }
            })
        }
    }

    private fun matchesSearch(product: com.example.moozik.models.Product, query: String): Boolean {
        if (query.isBlank()) return true
        val normalized = query.lowercase()
        return product.title.lowercase().contains(normalized) ||
            product.category.lowercase().contains(normalized)
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
    private var currentQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        renderCart(view, currentQuery)

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

    private fun renderCart(root: View, query: String = currentQuery) {
        currentQuery = query
        val items = CartStore.getCartItems(requireContext(), query)

        val recycler = root.findViewById<RecyclerView>(R.id.recyclerCartItems)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = CartAdapter(items) { cartItem ->
            CartStore.removeOneFromCart(requireContext(), cartItem.product.id)
            renderCart(root, currentQuery)
        }

        val subtotal = items.sumOf { parsePrice(it.product.price) * it.quantity }
        val shipping = if (items.isEmpty()) 0 else 500
        val total = subtotal + shipping

        root.findViewById<TextView>(R.id.textSubtotalValue).text = formatPkr(subtotal)
        root.findViewById<TextView>(R.id.textShippingValue).text = formatPkr(shipping)
        root.findViewById<TextView>(R.id.textTotalValue).text = formatPkr(total)
        root.findViewById<TextView>(R.id.textEmptyCart).apply {
            text = if (query.isNotBlank() && items.isEmpty()) "No matching items" else "Your cart is empty"
            visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun formatPkr(value: Int): String {
        return "PKR ${"%,d".format(value)}"
    }

    private fun parsePrice(value: String): Int {
        return value.filter { it.isDigit() }.toIntOrNull() ?: 0
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
