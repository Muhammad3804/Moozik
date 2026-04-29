package com.example.moozik.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moozik.R
import com.example.moozik.util.loadImageFromAssets
import com.example.moozik.models.CartItem

class CartAdapter(
    private val items: List<CartItem>,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_row, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.product.title
        holder.price.text = item.product.price
        holder.qty.text = "Qty: ${item.quantity}"
        holder.image.loadImageFromAssets(item.product.title, R.drawable.ic_image)
        holder.remove.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgCartProduct)
        val name: TextView = itemView.findViewById(R.id.txtCartName)
        val price: TextView = itemView.findViewById(R.id.txtCartPrice)
        val qty: TextView = itemView.findViewById(R.id.txtCartQty)
        val remove: TextView = itemView.findViewById(R.id.btnRemoveItem)
    }
}

