package com.example.projectone.salesperson.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.salesperson.salespersondatamodels.Orders
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

class OrderAdapter(
    private val context: Context,
    private val orders: List<Orders>,
    private val itemClickListener: OnItemClickListener
) : BaseAdapter() {

    init {
        Log.d("OrderAdapter", "Adapter initialized with ${orders.size} orders")
    }

    override fun getCount(): Int = orders.size

    override fun getItem(position: Int): Any = orders[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val order = getItem(position) as Orders
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.salespersonlistview, parent, false)

        // View bindings
        val profileImageView = view.findViewById<CircleImageView>(R.id.createOrderSalesPersonProfileImage)
        val fullNameTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonFullName)
        val orderDateTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonDoj)
        val sampleImageView = view.findViewById<ShapeableImageView>(R.id.createOrderSalesPersonSampleImage)
        val priceTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonPrice)
        val statusTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonTrackStatusTv)
        val ratingTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonRatingTv)
        val clientNameTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonClientName)
        val titleTextView = view.findViewById<TextView>(R.id.salespersonTVShowTitle)
        val descriptionTextView = view.findViewById<TextView>(R.id.createOrderSalesPersonDescription)


        if (!order.profileImageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(order.profileImageUrl)
                .placeholder(R.drawable.sample_logo)
                .into(profileImageView)
        } else {
            Log.d("OrderAdapter", "Profile Image URL is null or empty")
//            Toast.makeText(context, "Profile Image Url: ${order.imageUrl}", Toast.LENGTH_SHORT).show()
        }



        // Load images using Glide
        order.profileImageUrl.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.drawable.sample_logo)
                .into(profileImageView)
            Log.d("Glide OrderAdapter","salesersonprofileimageurl${order.profileImageUrl}")
        }

        order.imageUrl?.let {
            Glide.with(context)
                .load(it)
                .into(sampleImageView)
        }

        // Populate text views
        fullNameTextView.text = order.salesPersonName
        orderDateTextView.text = order.orderDate
        priceTextView.text = "${order.estimatedBudget}/-"
        statusTextView.text = order.orderStatus
        clientNameTextView.text = order.customerName
        titleTextView.text = order.productName
        descriptionTextView.text = order.productDescription
        ratingTextView.text = order.ratings?.toString() ?: "4.5"

        profileImageView.setOnClickListener {
            val empId = order.empId
            Log.d("OrderAdapter", "Profile Image clicked: $empId")
            if (!empId.isNullOrEmpty()) {
                itemClickListener.onProfileImageClick(empId)
            } else {
                Log.d("OrderAdapter", "Profile Image clicked but empId is null or empty")
            }
        }

        view.setOnClickListener {
            val orderId = order.orderId
            Log.d("OrderAdapter", "Item clicked: $orderId")
            if (!orderId.isNullOrEmpty()) {
                itemClickListener.onItemClick(orderId)
            } else {
                Log.d("OrderAdapter", "Item clicked but orderId is null or empty")
            }
        }

        return view
    }

    // Define a custom interface for item clicks
    interface OnItemClickListener {
        fun onItemClick(orderId: String)
        fun onProfileImageClick(userProfileId: String)
    }
}
