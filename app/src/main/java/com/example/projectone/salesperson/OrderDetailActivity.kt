package com.example.projectone.salesperson

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.example.projectone.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide
import com.example.projectone.salesperson.salespersondatamodels.Orders

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var toolbarOrderDetailActivity: androidx.appcompat.widget.Toolbar
    private lateinit var detailsalesPersonSampleImage: ShapeableImageView
    private lateinit var detailSalesPersonCallButton: ImageButton
    private lateinit var detailSalesPersonTvDetailPrice: TextView
    private lateinit var detailSalesPersonTvDetailOrderStatus: TextView
    private lateinit var detailSalesPersonTvDetailPaymentStatus: TextView
    private lateinit var detailSalesPersonTvDetailModeOfPayment: TextView
    private lateinit var detailSalesPersonTvDetailRatingTextView: TextView
    private lateinit var detailSalesPersonOrderExpectedDate: TextView
    private lateinit var detailSalesPersonTvDetailQuantity: TextView
    private lateinit var detailSalesPersonTvDetailProductId: TextView
    private lateinit var detailSalesPersonTvDetailClientId: TextView
    private lateinit var detailSalesPersonTvDetailClientFullName: TextView
    private lateinit var detailSalesPersonTvDetailClientMobileNo: TextView
    private lateinit var detailSalesPersonTvDetailClientCompany: TextView
    private lateinit var detailSalesPersonTvDetailClientLocation: TextView
    private lateinit var detailSalesPersonTvDetailCreatedDate: TextView
    private lateinit var detailSalesPersonTvDetailDueDate: TextView
    private lateinit var detailSalespersonTVShowTitle: TextView
    private lateinit var detailSalespersonTVShowDescription: TextView
        private lateinit var ivDelivered: ImageView
    private lateinit var ivReadyForDelivery:ImageView
    private lateinit var ivQualityCheck:ImageView
    private lateinit var ivInProduction:ImageView
    private lateinit var ivOrderReceived:ImageView
    private val orders = mutableListOf<Orders>()

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Initialize views
        toolbarOrderDetailActivity=findViewById(R.id.toolbarOrderDetailActivity)
        detailsalesPersonSampleImage = findViewById(R.id.orderDetailSalesPersonSampleImage)
        detailSalesPersonCallButton = findViewById(R.id.orderDetailSalesPersonCallButton)
        detailSalesPersonTvDetailPrice = findViewById(R.id.orderDetailSalesPersonTvDetailPrice)
        detailSalesPersonTvDetailOrderStatus = findViewById(R.id.orderDetailSalesPersonTvDetailOrderStatus)
        detailSalesPersonTvDetailPaymentStatus = findViewById(R.id.orderDetailSalesPersonTvDetailPaymentStatus)
        detailSalesPersonTvDetailModeOfPayment = findViewById(R.id.orderDetailSalesPersonTvDetailModeOfPayment)
        detailSalesPersonTvDetailRatingTextView = findViewById(R.id.orderDetailSalesPersonTvDetailRatingTextView)
        detailSalesPersonOrderExpectedDate = findViewById(R.id.orderDetailSalesPersonOrderExpectedDate)
        detailSalesPersonTvDetailQuantity = findViewById(R.id.orderDetailSalesPersonTvDetailQuantity)
        detailSalesPersonTvDetailProductId = findViewById(R.id.orderDetailSalesPersonTvDetailProductId)
        detailSalesPersonTvDetailClientId = findViewById(R.id.orderDetailSalesPersonTvDetailClientId)
        detailSalesPersonTvDetailClientFullName = findViewById(R.id.orderDetailSalesPersonTvDetailClientFullName)
        detailSalesPersonTvDetailClientMobileNo = findViewById(R.id.orderDetailSalesPersonTvDetailClientMobileNo)
        detailSalesPersonTvDetailClientCompany = findViewById(R.id.orderDetailSalesPersonTvDetailClientCompany)
        detailSalesPersonTvDetailClientLocation = findViewById(R.id.orderDetailSalesPersonTvDetailClientLocation)
        detailSalesPersonTvDetailCreatedDate = findViewById(R.id.orderDetailSalesPersonTvDetailCreatedDate)
        detailSalesPersonTvDetailDueDate = findViewById(R.id.orderDetailSalesPersonTvDetailDueDate)
        detailSalespersonTVShowTitle = findViewById(R.id.orderDetailSalespersonTVShowTitle)
        detailSalespersonTVShowDescription = findViewById(R.id.orderDetailSalespersonTVShowDescription)
                ivOrderReceived=findViewById(R.id.ivOrderRecieved)
        ivInProduction=findViewById(R.id.ivInProduction)
        ivQualityCheck=findViewById(R.id.ivQualityCheck)
        ivReadyForDelivery=findViewById(R.id.ivReadyForDelivery)
        ivDelivered=findViewById(R.id.ivDelivered)

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("orders")

        // Retrieve and display order details
        val orderId = intent.getStringExtra("ORDER_ID")
        if (orderId != null) {
            displayOrderDetails(orderId)
        } else {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if orderId is null
        }

        setSupportActionBar(toolbarOrderDetailActivity)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }





    private fun updateImageViewVisibility(order: Orders) {
        // Reset visibility for all images
        ivOrderReceived.visibility = View.GONE
        ivInProduction.visibility = View.GONE
        ivQualityCheck.visibility = View.GONE
        ivReadyForDelivery.visibility = View.GONE
        ivDelivered.visibility = View.GONE

        // Show images based on the order status
        when (order.orderStatus) {
            "Order Received" -> {
                ivOrderReceived.visibility = View.VISIBLE
            }
            "In Production" -> {
                ivOrderReceived.visibility = View.VISIBLE
                ivInProduction.visibility = View.VISIBLE
            }
            "Quality Check" -> {
                ivOrderReceived.visibility = View.VISIBLE
                ivInProduction.visibility = View.VISIBLE
                ivQualityCheck.visibility = View.VISIBLE
            }
            "Ready for Delivery" -> {
                ivOrderReceived.visibility = View.VISIBLE
                ivInProduction.visibility = View.VISIBLE
                ivQualityCheck.visibility = View.VISIBLE
                ivReadyForDelivery.visibility = View.VISIBLE
            }
            "Delivered" -> {
                ivOrderReceived.visibility = View.VISIBLE
                ivInProduction.visibility = View.VISIBLE
                ivQualityCheck.visibility = View.VISIBLE
                ivReadyForDelivery.visibility = View.VISIBLE
                ivDelivered.visibility = View.VISIBLE
            }
            else -> {
                // Handle any unexpected status
                Toast.makeText(this, "Unknown order status: ${order.orderStatus}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun displayOrderDetails(orderId: String) {
        databaseReference.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                if (order != null) {
                    // Populate views with order details
                    detailSalesPersonTvDetailClientFullName.text = order.customerName
                    detailSalesPersonTvDetailClientMobileNo.text = order.mobileNumber
                    detailSalesPersonTvDetailClientId.text = order.clientId
                    detailSalesPersonTvDetailCreatedDate.text = order.orderDate
                    detailSalesPersonOrderExpectedDate.text=order.dueDate
                    detailSalesPersonTvDetailDueDate.text = order.dueDate
                    detailSalesPersonTvDetailQuantity.text = order.orderQuantity
                    detailSalesPersonTvDetailProductId.text = order.orderId
                    detailSalesPersonTvDetailOrderStatus.text = order.orderStatus
                    detailSalesPersonTvDetailPaymentStatus.text = order.paymentStatus
                    detailSalesPersonTvDetailModeOfPayment.text = order.modeOfPayment
                    detailSalesPersonTvDetailPrice.text = order.estimatedBudget+"/-"
                    detailSalesPersonTvDetailClientCompany.text = order.companyName
                    detailSalesPersonTvDetailClientLocation.text = order.address
                    detailSalespersonTVShowTitle.text = order.productName
                    detailSalespersonTVShowDescription.text = order.productDescription

                    // Load image if URL is present
                    if (order.imageUrl != null) {
                        Glide.with(this@OrderDetailActivity)
                            .load(order.imageUrl)
                            .into(detailsalesPersonSampleImage)
                    }
                    supportActionBar?.title=order.salesPersonName?:""
                    supportActionBar?.subtitle=order.empId//"₹"+order.estimatedBudget+"/-"?:""


                    // Update ImageView visibility based on order status
                    updateImageViewVisibility(order)
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderDetailActivity, "Failed to load order details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
