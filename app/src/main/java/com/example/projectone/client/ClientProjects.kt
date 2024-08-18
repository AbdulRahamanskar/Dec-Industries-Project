package com.example.projectone.client

//import OrderAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.example.projectone.salesperson.OrderDetailActivity
import com.example.projectone.salesperson.RedirectUserprofile
import com.example.projectone.salesperson.adapters.OrderAdapter
//import com.example.projectone.salesperson.adapters.OrderAdapter
import com.example.projectone.salesperson.salespersondatamodels.Orders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ClientProjects : AppCompatActivity(), OrderAdapter.OnItemClickListener {
    private lateinit var clientHomeListView: ListView
    private lateinit var clientProjectBottom: BottomNavigationView
    private lateinit var clientId: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Orders>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_projects)

        clientId = intent.getStringExtra("CLIENT_ID") ?: ""
        clientHomeListView = findViewById(R.id.clientHomeListView)
        val toolbarClientProjects = findViewById<Toolbar>(R.id.toolbarClientProject)
        clientProjectBottom = findViewById(R.id.clientProjectBottom)
        clientProjectBottom.selectedItemId = R.id.clientProjectsMenu
        clientProjectBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
        setSupportActionBar(toolbarClientProjects)
        supportActionBar?.title = ""
        supportActionBar?.subtitle = "Projects"

        // Set up Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("orders")

        // Initialize adapter and set to ListView
        orderAdapter = OrderAdapter(this, orders, this) // Pass 'this' as the click listener
        clientHomeListView.adapter = orderAdapter

        // Fetch orders from Firebase
        fetchOrdersFromFirebase()

        // Load client data based on clientId
        loadClientData()
    }

    private fun fetchOrdersFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()  // Clear the old data
                for (data in snapshot.children) {
                    val order = data.getValue(Orders::class.java)
                    order?.let { orders.add(it) }
                }
                orderAdapter.notifyDataSetChanged()  // Notify adapter to update the ListView
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientProjects, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadClientData() {
        val clientRef = FirebaseDatabase.getInstance().getReference("users").child(clientId)
        clientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle client data here
                val clientName = snapshot.child("fullname").getValue(String::class.java)
                // Populate views with client data
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientProjects, "Failed to load client data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(orderId: String) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        startActivity(intent)
    }

    override fun onProfileImageClick(userProfileId: String) {
        val intent = Intent(this, RedirectUserprofile::class.java)
        intent.putExtra("USER_PROFILE_ID", userProfileId)
        startActivity(intent)
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == clientProjectBottom.selectedItemId) {
            return true
        }
        return when (itemId) {
            R.id.clientProjectsMenu -> {
                finish()
                true
            }
            R.id.clientSettingsMenu -> {
                startActivity(Intent(this@ClientProjects, ClientSettings::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.clientProfileMenu -> {
                startActivity(Intent(this@ClientProjects, ClientProfile::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            else -> false
        }
    }
}
