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
import com.example.projectone.manufacturer.adapters.ManufacturerUsersListsAdapter
import com.example.projectone.manufacturer.manufacturerdatamodels.Manufacturer
import com.example.projectone.salesperson.adapters.SalesPersonUserListAdapter
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminManufacturersUserLists : AppCompatActivity() {
    private lateinit var manufacturersUsersListView:ListView
    private lateinit var searchManufacturer:TextInputEditText
    private lateinit var database: DatabaseReference
    private lateinit var manufacturerAdapter: ManufacturerUsersListsAdapter
    private val manufacturers = mutableListOf<Manufacturer>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manufacturers_user_lists)

        manufacturersUsersListView=findViewById(R.id.lvManufacturers)
        searchManufacturer=findViewById(R.id.etSearchBar)

        val toolbarManufacturersUsersLists=findViewById<Toolbar>(R.id.toolbarManufacturersUsersLists)
        setSupportActionBar(toolbarManufacturersUsersLists)

        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle = "Manufacturers Users Lists"
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

        }
        database = FirebaseDatabase.getInstance().reference.child("users")
        fetchManufacturers()


        // Set up search functionality
        searchManufacturer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                manufacturerAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }


    private fun fetchManufacturers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                manufacturers.clear()
                for (userSnapshot in snapshot.children) {
                    val rawValue = userSnapshot.value
                    Log.d("FetchManufacturers", "Raw data: $rawValue")

                    try {
                        val manufacturer = userSnapshot.getValue(Manufacturer::class.java)
                        if (manufacturer != null && manufacturer.role == "manufacturer") {
                            manufacturers.add(manufacturer)
                            Log.d("FetchManufacturers", "Manufacturer added: $manufacturer")
                        } else {
                            Log.e("FetchManufacturers", "Error: Manufacturer data is null or role is not manufacturer for user ID ${userSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminManufacturersUserLists, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FetchManufacturers", "Error parsing manufacturer data", e)
                    }
                }

                manufacturerAdapter = ManufacturerUsersListsAdapter(this@AdminManufacturersUserLists, manufacturers)
                manufacturersUsersListView.adapter = manufacturerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminManufacturersUserLists, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




//    private fun fetchManufacturers() {
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                manufacturers.clear()
//                for (userSnapshot in snapshot.children) {
//                    try {
//                        val manufacturer = userSnapshot.getValue(Manufacturer::class.java)
//                        if (manufacturer != null && manufacturer.role == "manufacturer") {
//                            manufacturers.add(manufacturer)
//                            Log.d("FetchManufacturers", "Manufacturer added: $manufacturer")
////                            Toast.makeText(this@AdminSalesPersonsUsersLists, "SalesPerson added: $salesPerson", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Log.e("FetchManufacturers", "Error: Manufacturer data is null or role is not salesPersons for user ID ${userSnapshot.key}")
////                            Toast.makeText(this@AdminSalesPersonsUsersLists, "Error: Client data is null or role is not client for user ID ${userSnapshot.key}", Toast.LENGTH_SHORT).show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(this@AdminManufacturersUserLists, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                        Log.e("FetchMnufacturers", "Error parsing manufacturer data", e)
//                    }
//                }
//
//                manufacturerAdapter = ManufacturerUsersListsAdapter(this@AdminManufacturersUserLists, manufacturers)
//                manufacturersUsersListView.adapter = manufacturerAdapter
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@AdminManufacturersUserLists, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}