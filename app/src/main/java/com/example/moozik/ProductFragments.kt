package com.example.moozik

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.example.moozik.models.Product

class ProductFragments {

    class ProductDetailFragment : BaseScreenFragment(R.layout.fragment_product_detail) {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val toolbar = view.findViewById<Toolbar>(R.id.productToolbar)
            toolbar.navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_back)
            toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

            val product = readProductFromArgs() ?: return

            view.findViewById<TextView>(R.id.textProductTitle).text = product.title
            view.findViewById<TextView>(R.id.textProductRating).text = "Rating: ${product.rating}"
            view.findViewById<TextView>(R.id.textProductPrice).text = product.price
            view.findViewById<TextView>(R.id.textProductDescription).text = product.description
            // Append provided detailed Schecter description if available (or always append to ensure content)
            val extraDesc = "Schecter Synyster Gates Custom-S: The Ultimate Shred Machine\nTake your playing to the front lines with the Schecter Synyster Gates Custom-S. Designed specifically for the high-octane demands of Avenged Sevenfold’s lead guitarist, this isn't just a signature model—it’s a precision-engineered weapon for modern metal. Featuring the iconic pinstripe aesthetic and the revolutionary Sustainiac® system, this guitar is built for players who refuse to be limited by physics.\nKey Features\nInfinite Sustain with Sustainiac®: The secret to Syn’s haunting leads. The neck pickup doubles as a Sustainiac driver, allowing you to hold notes indefinitely with three modes: Fundamental, Harmonic, or a blend of both.\nSchecter USA Signature Pickups: In the bridge, you’ll find the hand-wound USA Synyster Gates Signature humbucker. Built with Ceramic-8 magnets, it delivers crushing output without sacrificing clarity or articulation.\nUltra-Fast Playing Profile: The 3-piece mahogany neck features an Ultra Thin ‘C’ profile and a 16” radius ebony fingerboard. Whether you’re sweeping through arpeggios or digging into heavy riffs, the neck feel is effortless.\nFloyd Rose 1500 Series Bridge: Dive bomb, squeal, and flutter with total confidence. The upgraded 1500 Series bridge offers stainless steel hardware and a push-in arm for superior tuning stability and a smoother feel than standard systems.\nIconic \"Avenger\" Body Shape: Crafted from solid mahogany, the distinctive Avenger body provides a deep, resonant low-end and incredible upper-fret access thanks to the Ultra Access neck joint.\nTechnical Specifications\nBody Material: Mahogany\nNeck Material: 3-pc Mahogany with Carbon Fiber Reinforcement Rods\nFretboard: Ebony with Pearloid \"SYN\" and \"Death Bat\" Inlays\nFrets: 24 X-Jumbo\nBridge Pickup: Schecter USA Synyster Gates Signature Humbucker\nNeck Pickup: Sustainiac®\nControls: Volume/Tone/3-Way (Pickup) Switch/2-Way On-Off Sustainiac Switch/3-Way Sustainiac Mode Switch\nScale Length: 25.5\" (648mm)"

            // Append the extra description after the product description so it's visible under Description title
            val descView = view.findViewById<TextView>(R.id.textProductDescription)
            descView.text = product.description + "\n\n" + extraDesc

            val img = view.findViewById<ImageView>(R.id.imageProductLarge)
            // Use a generic placeholder for now (XML vector drawable)
            img.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_image))

            view.findViewById<Button>(R.id.btnBuyNow).setOnClickListener {
                // Since there's no backend, show simple feedback using navigation or a Toast
                android.widget.Toast.makeText(requireContext(), "Buy Now: ${product.title}", android.widget.Toast.LENGTH_SHORT).show()
            }

            view.findViewById<Button>(R.id.btnAddCart).setOnClickListener {
                android.widget.Toast.makeText(requireContext(), "Added to cart: ${product.title}", android.widget.Toast.LENGTH_SHORT).show()
            }

            view.findViewById<ImageView>(R.id.btnChatSeller).setOnClickListener {
                android.widget.Toast.makeText(requireContext(), "Chat with seller (mock)", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        private fun readProductFromArgs(): Product? {
            val args = arguments ?: return null
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getParcelable(ARG_PRODUCT, Product::class.java)
            } else {
                @Suppress("DEPRECATION")
                args.getParcelable(ARG_PRODUCT)
            }
        }

        companion object {
                private const val ARG_PRODUCT = "arg_product"

                fun newInstance(product: Product): ProductDetailFragment {
                    return ProductDetailFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable(ARG_PRODUCT, product)
                        }
                    }
                }
        }
    }
}

