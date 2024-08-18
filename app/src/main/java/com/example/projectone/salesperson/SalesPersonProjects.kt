package com.example.projectone.salesperson

//import OrderAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectone.R
import com.example.projectone.salesperson.adapters.OrderAdapter // Correct import for OrderAdapter
import com.example.projectone.salesperson.salespersondatamodels.Orders // Correct import for Orders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class SalesPersonProjects : AppCompatActivity(), OrderAdapter.OnItemClickListener {

    private lateinit var listView: ListView
    private lateinit var salesBottomMenu: BottomNavigationView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Orders>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_projects)


        listView = findViewById(R.id.salesPersonProjectsListView)
        salesBottomMenu = findViewById(R.id.salesPersonBottomProjects)
        salesBottomMenu.selectedItemId = R.id.salesPersonProjectsMenu
        salesBottomMenu.setOnNavigationItemSelectedListener { item ->
            navigationHandle(item.itemId)
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("orders")

        orderAdapter = OrderAdapter(this, orders, this)
        listView.adapter = orderAdapter

        fetchOrdersFromFirebase()
    }

    private fun fetchOrdersFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (data in snapshot.children) {
                    val order = data.getValue(Orders::class.java)
                    order?.let { orders.add(it) }
                }
                orderAdapter.notifyDataSetChanged()
//                updateImageViewVisibility()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesPersonProjects, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }


//    private fun updateImageViewVisibility() {
//        // Default visibility
//        ivOrderReceived.visibility = View.VISIBLE
//        ivInProduction.visibility = View.GONE
//        ivQualityCheck.visibility = View.GONE
//        ivReadyForDelivery.visibility = View.GONE
//        ivDelivered.visibility = View.GONE
//
//        for (order in orders) {
//            when (order.orderStatus) {
//                "Order Received" -> ivOrderReceived.visibility = View.VISIBLE
//                "In Production" -> ivInProduction.visibility = View.VISIBLE
//                "Quality Check" -> ivQualityCheck.visibility = View.VISIBLE
//                "Ready for Delivery" -> ivReadyForDelivery.visibility = View.VISIBLE
//                "Delivered" -> ivDelivered.visibility = View.VISIBLE
//            }
//        }
//    }



    override fun onProfileImageClick(empId: String) {
        Log.d("SalesPersonProjects", "Profile Image clicked: $empId")
        val intent = Intent(this@SalesPersonProjects, RedirectUserprofile::class.java)
        intent.putExtra("USER_PROFILE_ID", empId)
        startActivity(intent)
    }


    override fun onItemClick(orderId: String) {
        Log.d("SalesPersonProjects", "Item clicked: $orderId")
        val intent = Intent(this@SalesPersonProjects, OrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.salespersonnotifications,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.salesPersonNotificationsMenu->{
                startActivity(Intent(this@SalesPersonProjects,SalesPersonNotificationsActivity::class.java))
            return true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun navigationHandle(itemId: Int): Boolean {
        if (itemId == salesBottomMenu.selectedItemId) {
            return true
        }
        return when (itemId) {
            R.id.salesPersonProjectsMenu -> true
            R.id.salesPersonCreateOrderMenu -> {
                startActivity(Intent(this, SalesPersonCreateOrder::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.salesPersonProfileMenu -> {
                startActivity(Intent(this, SalesPersonProfile::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.salesPersonSettingsMenu -> {
                startActivity(Intent(this, SalesPersonMoreSettings::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            else -> false
        }
    }
}
