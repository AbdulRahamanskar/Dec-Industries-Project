package com.example.projectone.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.example.projectone.admin.adapters.UpdateOrderStatusAdapter
import com.example.projectone.salesperson.salespersondatamodels.Orders
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingProjects : AppCompatActivity() {
    private lateinit var toolbarPendingProjects:Toolbar
    private lateinit var ppListView: ListView
    private lateinit var updateOrderStatusAdapter: UpdateOrderStatusAdapter
    private val orders = mutableListOf<Orders>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_projects)
        ppListView=findViewById(R.id.adminOrderStatusUpdateListView)

        toolbarPendingProjects=findViewById(R.id.toolbarPendingProjects)
        setSupportActionBar(toolbarPendingProjects)
        supportActionBar?.apply {
            supportActionBar?.title=""
            supportActionBar?.subtitle="Pending Projects"
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }
        updateOrderStatusAdapter = UpdateOrderStatusAdapter(this, orders)
        ppListView.adapter = updateOrderStatusAdapter
        fetchOrders()


    }

    private fun fetchOrders() {
        // Assuming you have a reference to your Firebase database
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Orders::class.java)
                    order?.let { orders.add(it) }
                }
                updateOrderStatusAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
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