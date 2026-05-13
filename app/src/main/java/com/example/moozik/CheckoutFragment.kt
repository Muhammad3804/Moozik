package com.example.moozik

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.moozik.data.CartStore
import com.example.moozik.data.FirestoreCartRepository
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckoutFragment : BaseScreenFragment(R.layout.fragment_checkout) {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private var selectedPaymentMethod: PaymentMethod? = null
    private var pendingOrderSubmission: Boolean = false

    private lateinit var editFullName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editStreet: EditText
    private lateinit var editCity: EditText
    private lateinit var editProvince: EditText
    private lateinit var editPostalCode: EditText
    private lateinit var cardCreditDebit: MaterialCardView
    private lateinit var cardCashOnDelivery: MaterialCardView
    private lateinit var textCreditTitle: android.widget.TextView
    private lateinit var textCreditSubtitle: android.widget.TextView
    private lateinit var textCashTitle: android.widget.TextView
    private lateinit var textCashSubtitle: android.widget.TextView

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingOrderSubmission) {
            pendingOrderSubmission = false
            sendNotificationAndFinishOrder()
        } else if (pendingOrderSubmission) {
            pendingOrderSubmission = false
            Toast.makeText(requireContext(), "Notification permission is required to place the order.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editFullName = view.findViewById(R.id.editFullNameCheckout)
        editPhone = view.findViewById(R.id.editPhoneCheckout)
        editStreet = view.findViewById(R.id.editStreetCheckout)
        editCity = view.findViewById(R.id.editCityCheckout)
        editProvince = view.findViewById(R.id.editProvinceCheckout)
        editPostalCode = view.findViewById(R.id.editPostalCodeCheckout)

        cardCreditDebit = view.findViewById(R.id.cardCreditDebit)
        cardCashOnDelivery = view.findViewById(R.id.cardCashOnDelivery)
        textCreditTitle = view.findViewById(R.id.textCreditTitle)
        textCreditSubtitle = view.findViewById(R.id.textCreditSubtitle)
        textCashTitle = view.findViewById(R.id.textCashTitle)
        textCashSubtitle = view.findViewById(R.id.textCashSubtitle)

        view.findViewById<Button>(R.id.btnSaveAddressCheckout).setOnClickListener {
            saveAddress()
        }
        view.findViewById<Button>(R.id.btnPlaceOrderCheckout).setOnClickListener {
            placeOrder()
        }

        cardCreditDebit.setOnClickListener {
            updateSelectedPaymentMethod(PaymentMethod.CARD)
        }
        cardCashOnDelivery.setOnClickListener {
            updateSelectedPaymentMethod(PaymentMethod.COD)
        }

        createNotificationChannel()
        loadSavedAddress()
        updateSelectedPaymentMethod(null)

        bindBottomNav(
            root = view,
            selectedIndex = 2,
            onStore = {
                (activity as? MainActivity)?.showFragment(
                    StoreFragment.newInstance(resolveCurrentUserName())
                )
            },
            onLessons = { navigateTo(LessonsFragment()) },
            onCart = { navigateTo(CartFragment()) },
            onProfile = { navigateTo(ProfileFragment()) }
        )
    }

    private fun loadSavedAddress() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                doc.getString("fullName")?.let(editFullName::setText)
                doc.getString("phone")?.let(editPhone::setText)
                doc.getString("street")?.let(editStreet::setText)
                doc.getString("city")?.let(editCity::setText)
                doc.getString("province")?.let(editProvince::setText)
                doc.getString("postalCode")?.let(editPostalCode::setText)
            }
    }

    private fun saveAddress() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(requireContext(), "Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val address = collectAddressOrShowToast() ?: return
        val payload = hashMapOf<String, Any>(
            "fullName" to address.fullName,
            "phone" to address.phone,
            "street" to address.street,
            "city" to address.city,
            "province" to address.province,
            "postalCode" to address.postalCode
        )

        firestore.collection("users")
            .document(userId)
            .update(payload)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Address saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                firestore.collection("users")
                    .document(userId)
                    .set(payload, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Address saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), error.message ?: "Failed to save address.", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun placeOrder() {
        if (collectAddressOrShowToast() == null) return
        if (selectedPaymentMethod == null) {
            Toast.makeText(requireContext(), "Please select a payment method.", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingOrderSubmission = true
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        sendNotificationAndFinishOrder()
    }

    private fun sendNotificationAndFinishOrder() {
        val context = requireContext()
        val appContext = context.applicationContext
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Order Confirmed! 🎸")
            .setContentText("Your order is on the way!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            Toast.makeText(context, "Notification permission is required to place the order.", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()

        val userId = auth.currentUser?.uid
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            CartStore.clearCart(appContext)
            if (userId != null) {
                FirestoreCartRepository().clearCart(userId)
            }

            withContext(Dispatchers.Main) {
                val activity = activity as? MainActivity ?: return@withContext
                activity.supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                activity.showFragment(StoreFragment.newInstance(resolveCurrentUserName()))
            }
        }
    }

    private fun resolveCurrentUserName(fallback: String = "Guest User"): String {
        val user = auth.currentUser
        return user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@").orEmpty().takeIf { it.isNotBlank() }
            ?: fallback
    }

    private fun updateSelectedPaymentMethod(method: PaymentMethod?) {
        selectedPaymentMethod = method

        val selectedBackground = "#FFF1F1".toColorInt()
        val selectedStroke = "#EA4243".toColorInt()
        val selectedText = "#EA4243".toColorInt()
        val unselectedBackground = "#FFFFFF".toColorInt()
        val unselectedStroke = "#E6E6E6".toColorInt()
        val unselectedText = "#1A1A1A".toColorInt()

        val creditSelected = method == PaymentMethod.CARD
        val codSelected = method == PaymentMethod.COD

        cardCreditDebit.setCardBackgroundColor(if (creditSelected) selectedBackground else unselectedBackground)
        cardCreditDebit.strokeColor = if (creditSelected) selectedStroke else unselectedStroke
        cardCreditDebit.strokeWidth = 2

        cardCashOnDelivery.setCardBackgroundColor(if (codSelected) selectedBackground else unselectedBackground)
        cardCashOnDelivery.strokeColor = if (codSelected) selectedStroke else unselectedStroke
        cardCashOnDelivery.strokeWidth = 2

        textCreditTitle.setTextColor(if (creditSelected) selectedText else unselectedText)
        textCreditSubtitle.setTextColor(if (creditSelected) selectedText else unselectedText)
        textCashTitle.setTextColor(if (codSelected) selectedText else unselectedText)
        textCashSubtitle.setTextColor(if (codSelected) selectedText else unselectedText)
    }

    private fun collectAddressOrShowToast(): AddressData? {
        val fullName = editFullName.text?.toString()?.trim().orEmpty()
        val phone = editPhone.text?.toString()?.trim().orEmpty()
        val street = editStreet.text?.toString()?.trim().orEmpty()
        val city = editCity.text?.toString()?.trim().orEmpty()
        val province = editProvince.text?.toString()?.trim().orEmpty()
        val postalCode = editPostalCode.text?.toString()?.trim().orEmpty()

        if (fullName.isBlank() || phone.isBlank() || street.isBlank() || city.isBlank() || province.isBlank() || postalCode.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all address fields.", Toast.LENGTH_SHORT).show()
            return null
        }

        return AddressData(fullName, phone, street, city, province, postalCode)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Moozik Orders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for confirmed Moozik orders"
        }
        val manager = requireContext().getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private data class AddressData(
        val fullName: String,
        val phone: String,
        val street: String,
        val city: String,
        val province: String,
        val postalCode: String,
    )

    private enum class PaymentMethod {
        CARD,
        COD
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "moozik_orders"
        private const val NOTIFICATION_ID = 1
    }
}





