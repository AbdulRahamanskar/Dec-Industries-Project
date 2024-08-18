package com.example.projectone.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.example.projectone.client.adapters.ClientAdapter
import com.example.projectone.client.clientdatamodels.Client
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminClientsUsersLists : AppCompatActivity() {
    private lateinit var searchClientsTIET: TextInputEditText
    private lateinit var adminClientListView: ListView
    private lateinit var database: DatabaseReference
    private lateinit var clientAdapter: ClientAdapter
    private val clients = mutableListOf<Client>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_clients_users_lists)

        // Views
        searchClientsTIET = findViewById(R.id.searchClientsTIET)
        adminClientListView = findViewById(R.id.adminClientListView)
        val toolbarAdminClientUsersLists = findViewById<Toolbar>(R.id.toolbarClientUsersLists)
        setSupportActionBar(toolbarAdminClientUsersLists)

        supportActionBar?.apply {
            title = ""
            subtitle = "Clients Users Lists"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        database = FirebaseDatabase.getInstance().reference.child("users")

        // Fetch data
        fetchClients()

        // Set up search functionality
        searchClientsTIET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clientAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchClients() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clients.clear()
                for (userSnapshot in snapshot.children) {
                    try {
                        val client = userSnapshot.getValue(Client::class.java)
                        if (client != null && client.role == "client") {
                            clients.add(client)
                            Log.d("FetchClients", "Client added: $client")
                        } else {
                            Log.e("FetchClients", "Error: Client data is null or role is not client for user ID ${userSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminClientsUsersLists, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FetchClients", "Error parsing client data", e)
                    }
                }

                clientAdapter = ClientAdapter(this@AdminClientsUsersLists, clients)
                adminClientListView.adapter = clientAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminClientsUsersLists, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
