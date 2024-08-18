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

class CompletedProjects : AppCompatActivity() {
    private lateinit var cpListView:ListView
    private lateinit var toolbarCompletedProjects:Toolbar
    private lateinit var completedOrdersAdapter: UpdateOrderStatusAdapter
    private val completedOrders = mutableListOf<Orders>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_projects)
        cpListView=findViewById(R.id.adminOrderStatusUpdateCompletedListView)
        toolbarCompletedProjects=findViewById(R.id.toolbarCompletedProjects)
        setSupportActionBar(toolbarCompletedProjects)
        supportActionBar?.apply {
            supportActionBar?.title=""
            supportActionBar?.subtitle="Completed Projects"
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }
        completedOrdersAdapter = UpdateOrderStatusAdapter(this, completedOrders)
        cpListView.adapter = completedOrdersAdapter
        fetchCompletedOrders()

    }
    private fun fetchCompletedOrders() {
        val database = FirebaseDatabase.getInstance()
        val completedOrdersRef = database.getReference("completed_orders")

        completedOrdersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrders.clear()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Orders::class.java)
                    order?.let { completedOrders.add(it) }
                }
                completedOrdersAdapter.notifyDataSetChanged()
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