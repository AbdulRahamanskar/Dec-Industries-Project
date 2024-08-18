package com.example.projectone.admin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.salesperson.salespersondatamodels.Orders
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import de.hdodenhof.circleimageview.CircleImageView


// OrderAdapter.kt
class UpdateOrderStatusAdapter(
    context: Context,
    private val orders: List<Orders>

) : ArrayAdapter<Orders>(context, R.layout.update_order_status_list, orders) {

    private val statuses = listOf(
        "Order Received",
        "In Production",
        "Quality Check",
        "Ready for Delivery",
        "Delivered")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.update_order_status_list, parent, false)

        val order = orders[position]
        val salespersonImage = view.findViewById<CircleImageView>(R.id.salespersonImage)
        val salespersonName = view.findViewById<TextView>(R.id.salespersonName)
        val salespersonId = view.findViewById<TextView>(R.id.salespersonId)
        val orderId = view.findViewById<TextView>(R.id.orderId)
        val orderStatus = view.findViewById<TextView>(R.id.orderStatus)
        val orderDate = view.findViewById<TextView>(R.id.orderDate)
        val changeStatusButton = view.findViewById<AppCompatButton>(R.id.changeStatusButton)

        // Set values from the order object
        salespersonName.text = order.salesPersonName
        salespersonId.text = "ID: ${order.empId}"
        orderId.text = "orderId: ${order.orderId}"
        orderStatus.text = order.orderStatus
        orderDate.text = "Date: ${order.orderDate}"
        // Set image and button click listener if needed
        Glide.with(context)
            .load(order.imageUrl)
            .placeholder(R.drawable.sample_logo)
            .into(salespersonImage)




        // Button visibility and click listener
        if (order.orderStatus == "Delivered") {
            changeStatusButton.visibility = View.GONE
        } else {
            changeStatusButton.visibility = View.VISIBLE

            changeStatusButton.setOnClickListener {
                // Determine the current status index
                val currentStatusIndex = statuses.indexOf(order.orderStatus)
                // Compute the next status index
                val nextStatusIndex = (currentStatusIndex + 1) % statuses.size
                // Update the order status
                val newStatus = statuses[nextStatusIndex]
                order.orderStatus = newStatus
                orderStatus.text = newStatus
                changeStatusButton.text = newStatus

                // Update status in Firebase
                val database = FirebaseDatabase.getInstance()
                val ordersRef = database.getReference("orders").child(order.orderId ?: "")
                ordersRef.child("orderStatus").setValue(newStatus)

                // Move to CompletedProjects if delivered
                if (newStatus == "Delivered") {
                    ordersRef.removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Optionally, you can also add the order to the completed projects database
                            val completedOrdersRef = database.getReference("completed_orders").child(order.orderId ?: "")
                            completedOrdersRef.setValue(order)

                            // Increment sales target completed count
                            val salesPersonRef = database.getReference("users").child(order.empId ?: "")
                            salesPersonRef.child("salesTargetsCompleted").runTransaction(object : Transaction.Handler {
                                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                    val currentCount = mutableData.getValue(Int::class.java) ?: 0
                                    mutableData.value = currentCount + 1
                                    return Transaction.success(mutableData)
                                }

                                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                                    if (databaseError != null) {
                                        Log.w("UpdateOrderStatusAdapter", "Failed to increment sales targets completed", databaseError.toException())
                                    }
                                }
                            })
                        }
                    }
                }
            }








//            changeStatusButton.setOnClickListener {
//                // Determine the current status index
//                val currentStatusIndex = statuses.indexOf(order.orderStatus)
//                // Compute the next status index
//                val nextStatusIndex = (currentStatusIndex + 1) % statuses.size
//                // Update the order status
//                val newStatus = statuses[nextStatusIndex]
//                order.orderStatus = newStatus
//                orderStatus.text = newStatus
//                changeStatusButton.text = newStatus
//
//                // Update status in Firebase
//                val database = FirebaseDatabase.getInstance()
//                val ordersRef = database.getReference("orders").child(order.orderId ?: "")
//                ordersRef.child("orderStatus").setValue(newStatus)
//
//                // Move to CompletedProjects if delivered
//                if (newStatus == "Delivered") {
//                    ordersRef.removeValue().addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            // Optionally, you can also add the order to the completed projects database
//                            val completedOrdersRef = database.getReference("completed_orders").child(order.orderId ?: "")
//                            completedOrdersRef.setValue(order)
//                        }
//                    }
//                }
//            }
        }






        return view
    }
}
