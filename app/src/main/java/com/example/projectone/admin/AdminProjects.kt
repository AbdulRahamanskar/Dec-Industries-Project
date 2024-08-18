package com.example.projectone.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.example.projectone.salesperson.OrderDetailActivity
import com.example.projectone.salesperson.RedirectUserprofile
import com.example.projectone.salesperson.adapters.OrderAdapter
import com.example.projectone.salesperson.salespersondatamodels.Orders
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProjects : AppCompatActivity(),OrderAdapter.OnItemClickListener {
    private lateinit var lvExploreProjects:ListView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var clientId: String
    private val orders = mutableListOf<Orders>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_projects)
        lvExploreProjects=findViewById(R.id.adminProjectsListView)

        val toolbarAdminOrdersList=findViewById<Toolbar>(R.id.AdminToolbarOrdersLists)
        setSupportActionBar(toolbarAdminOrdersList)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.subtitle="Explore Projects"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("orders")

        orderAdapter = OrderAdapter(this, orders,this)
        lvExploreProjects.adapter = orderAdapter

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
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminProjects, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(orderId: String) {
        Log.d("AdminProjects", "Item clicked: $orderId")
        val intent = Intent(this@AdminProjects, OrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        startActivity(intent)
    }

    override fun onProfileImageClick(userProfileId: String) {
        Log.d("AdminProjects", "Profile Image clicked: $userProfileId")
        val intent = Intent(this@AdminProjects, RedirectUserprofile::class.java)
        intent.putExtra("USER_PROFILE_ID", userProfileId)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

}