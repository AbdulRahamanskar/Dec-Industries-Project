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
import com.example.projectone.R
import com.example.projectone.client.adapters.ClientAdapter
import com.example.projectone.client.clientdatamodels.Client
import com.example.projectone.salesperson.adapters.SalesPersonUserListAdapter
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminSalesPersonsUsersLists : AppCompatActivity() {
    private lateinit var lvAdminSalesPerson:ListView
    private lateinit var tietSearchAdminSalesPerson:TextInputEditText
    private lateinit var database: DatabaseReference
    private lateinit var salesPersonAdapter: SalesPersonUserListAdapter
    private val salespersons = mutableListOf<SalesPerson>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sales_persons_users_lists)

        tietSearchAdminSalesPerson=findViewById(R.id.tietSearchAdminSalesPerson)
        lvAdminSalesPerson=findViewById(R.id.lvAdminSalesPerson)

        val toolbarSalesPersonsUsersLists=findViewById<Toolbar>(R.id.toolbarSalesPersonsUsersLists)
        setSupportActionBar(toolbarSalesPersonsUsersLists)

        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle = "Sales Persons Users Lists"
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

        }

        database = FirebaseDatabase.getInstance().reference.child("users")

        // Fetch data
        fetchSalesPersons()

        // Set up search functionality
        tietSearchAdminSalesPerson.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                salesPersonAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }



    private fun fetchSalesPersons() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                salespersons.clear()
                for (userSnapshot in snapshot.children) {
                    try {
                        val salesPerson = userSnapshot.getValue(SalesPerson::class.java)
                        if (salesPerson != null && salesPerson.role == "salesperson") {
                            salespersons.add(salesPerson)
                            Log.d("FetchSalesPersons", "SalesPerson added: $salesPerson")
//                            Toast.makeText(this@AdminSalesPersonsUsersLists, "SalesPerson added: $salesPerson", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("FetchSalesPerson", "Error: Salesperson data is null or role is not salesPersons for user ID ${userSnapshot.key}")
//                            Toast.makeText(this@AdminSalesPersonsUsersLists, "Error: Client data is null or role is not client for user ID ${userSnapshot.key}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminSalesPersonsUsersLists, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FetchSalesPersons", "Error parsing salesperson data", e)
                    }
                }

                salesPersonAdapter = SalesPersonUserListAdapter(this@AdminSalesPersonsUsersLists, salespersons)
                lvAdminSalesPerson.adapter = salesPersonAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminSalesPersonsUsersLists, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
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