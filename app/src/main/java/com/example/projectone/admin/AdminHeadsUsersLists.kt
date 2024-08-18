package com.example.projectone.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.client.adapters.ClientAdapter
import com.example.projectone.client.clientdatamodels.Client
import com.example.projectone.head.adapters.AdminSalesHeadAdapter
import com.example.projectone.head.salesheaddatamodels.SalesHead
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminHeadsUsersLists : AppCompatActivity() {
    private lateinit var lvAdminSalesHead:ListView
    private lateinit var tietAdminSalesHeadSearch:TextInputEditText
    private lateinit var database: DatabaseReference
    private lateinit var salesHeadAdapter: AdminSalesHeadAdapter
    private val salesHeads = mutableListOf<SalesHead>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_heads_users_lists)
        lvAdminSalesHead=findViewById(R.id.lvAdminSalesHead)
        tietAdminSalesHeadSearch=findViewById(R.id.tietAdminSalesHeadSearch)

        val toolbarHeadUsersLists=findViewById<Toolbar>(R.id.toolbarHeadUsersLists)
        setSupportActionBar(toolbarHeadUsersLists)

        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle = "Head Users Lists"
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

        }
        // Clear Glide's memory and disk cache
        Glide.get(this).clearMemory()
        Thread {
            Glide.get(this).clearDiskCache()
        }.start()


        database = FirebaseDatabase.getInstance().reference.child("users")

        // Fetch data
        fetchSalesHead()

        // Set up search functionality
        tietAdminSalesHeadSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                salesHeadAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun fetchSalesHead() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                salesHeads.clear()
                for (userSnapshot in snapshot.children) {
                    try {
                        val salesHead = userSnapshot.getValue(SalesHead::class.java)
                        if (salesHead != null && salesHead.role == "saleshead") {
                            salesHeads.add(salesHead)
                            Log.d("FetchSalesHeads", "Sales Heads added: $salesHeads")
                        } else {
                            Log.e("FetchSalesHeads", "Error: SalesHeads data is null or role is not client for user ID ${userSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminHeadsUsersLists, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FetchClients", "Error parsing client data", e)
                    }
                }

                salesHeadAdapter = AdminSalesHeadAdapter(this@AdminHeadsUsersLists, salesHeads)
                lvAdminSalesHead.adapter = salesHeadAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminHeadsUsersLists, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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