package com.example.projectone.head

import android.content.Intent
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
import com.example.projectone.head.adapters.CreateSalesPersonGroupAdapter
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SalesHeadCreateSalesPersonTeam : AppCompatActivity() {
    private lateinit var searchSalesPerson: TextInputEditText
    private lateinit var lvSalesPerson: ListView
    private lateinit var fabCreateGroup: FloatingActionButton
    private lateinit var database: DatabaseReference
    private lateinit var createGroupOfSalesPersonAdapter: CreateSalesPersonGroupAdapter
    private val creategroupsofsalespersonsteam = mutableListOf<SalesPerson>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_create_sales_person_team)

        searchSalesPerson = findViewById(R.id.tietSearchSalesPersonsToAddInTeam)
        lvSalesPerson = findViewById(R.id.lvSelectSalesPerson)
        fabCreateGroup = findViewById(R.id.fabCreateGroup)

        val toolbarSalesHeadCreateTeams = findViewById<Toolbar>(R.id.toolbarSalesHeadCreateTeams)
        setSupportActionBar(toolbarSalesHeadCreateTeams)
        supportActionBar?.title = ""
        supportActionBar?.subtitle = "Create Team"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)

        database = FirebaseDatabase.getInstance().reference.child("users")

        fetchSalesPersons()

        // Set up search functionality
        searchSalesPerson.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createGroupOfSalesPersonAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle FAB click
        fabCreateGroup.setOnClickListener {
            val selectedSalesPersons = createGroupOfSalesPersonAdapter.getSelectedSalesPersons()
            if (selectedSalesPersons.size < 1) {
                Toast.makeText(this, "You need to select at least 1 salesperson to create a group.", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to CreateGroup activity
                navigateToCreateGroup(selectedSalesPersons)
            }
        }
    }

    private fun navigateToCreateGroup(selectedSalesPersons: List<SalesPerson>) {
        val intent = Intent(this, SalesHeadCreateGroup::class.java)
        // Pass selected salespersons' IDs as a list
        intent.putStringArrayListExtra("selectedSalesPersonIds", ArrayList(selectedSalesPersons.map { it.empId }))
        startActivity(intent)
    }

    private fun fetchSalesPersons() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                creategroupsofsalespersonsteam.clear()
                for (userSnapshot in snapshot.children) {
                    try {
                        val salesPerson = userSnapshot.getValue(SalesPerson::class.java)
                        if (salesPerson != null && salesPerson.role == "salesperson") {
                            creategroupsofsalespersonsteam.add(salesPerson)
                            Log.d("FetchSalesPersons", "SalesPerson added: $salesPerson")
                        } else {
                            Log.e("FetchSalesPerson", "Error: Salesperson data is null or role is not salesperson for user ID ${userSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@SalesHeadCreateSalesPersonTeam, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("FetchSalesPersons", "Error parsing salesperson data", e)
                    }
                }

                createGroupOfSalesPersonAdapter = CreateSalesPersonGroupAdapter(this@SalesHeadCreateSalesPersonTeam, creategroupsofsalespersonsteam)
                lvSalesPerson.adapter = createGroupOfSalesPersonAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesHeadCreateSalesPersonTeam, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentSalesHeadId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userRole = dataSnapshot.child("role").getValue(String::class.java)
                    if (userRole == "saleshead") {
                        val salesHeadId = dataSnapshot.child("salesHeadId").getValue(String::class.java)
                        if (salesHeadId != null) {
                            // Use the SalesHead ID as needed
                            Log.d("SalesHead", "SalesHead ID: $salesHeadId")
                        } else {
                            Log.e("SalesHead", "SalesHead ID not found for user $userId")
                        }
                    } else {
                        Log.e("SalesHead", "Current user is not a SalesHead")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SalesHead", "Failed to retrieve user data: ${databaseError.message}")
                }
            })
        } else {
            Log.e("SalesHead", "No authenticated user found")
        }
        return "currentSalesHeadId" // Placeholder
    }
    private fun removeSalesPersonFromGroup(salesPersonId: String) {
        database.child("users").child(salesPersonId).child("assignedGroupId").get().addOnSuccessListener {
            val groupId = it.getValue(String::class.java)
            if (groupId != null && groupId != "none") {
                database.child("groups").child(groupId).child(salesPersonId).removeValue()
                    .addOnSuccessListener {
                        database.child("users").child(salesPersonId).child("assignedGroupId").setValue("none")
                            .addOnSuccessListener {
                                Toast.makeText(this, "Salesperson removed from group successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to remove salesperson: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to remove salesperson from group: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Salesperson is not assigned to any group.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createGroup(selectedSalesPersons: List<SalesPerson>, salesHeadId: String) {
        val groupId = database.child("groups").push().key
        if (groupId != null) {
            val groupMap = mutableMapOf<String, Boolean>()
            val salesPersonIds = selectedSalesPersons.map { it.empId }

            database.child("salespersons").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var canCreateGroup = true

                    for (salesPersonId in salesPersonIds) {
                        val salesperson = snapshot.child(salesPersonId).getValue(SalesPerson::class.java)
                        if (salesperson != null) {
                            val assignedGroupId = salesperson.assignedGroupId
                            val salespersonSalesHeadId = salesperson.salesHeadId

                            if (assignedGroupId.isNotEmpty() && assignedGroupId != "none") {
                                Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                                    "Salesperson $salesPersonId is already in a group managed by another SalesHead.",
                                    Toast.LENGTH_SHORT).show()
                                canCreateGroup = false
                                break
                            } else if (salespersonSalesHeadId != salesHeadId) {
                                Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                                    "Salesperson $salesPersonId is not managed by this SalesHead.",
                                    Toast.LENGTH_SHORT).show()
                                canCreateGroup = false
                                break
                            } else {
                                groupMap[salesPersonId] = true
                            }
                        } else {
                            Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                                "Salesperson ID $salesPersonId is invalid.",
                                Toast.LENGTH_SHORT).show()
                            canCreateGroup = false
                            break
                        }
                    }

                    if (canCreateGroup) {
                        database.child("groups").child(groupId).setValue(groupMap)
                            .addOnSuccessListener {
                                for (salesPersonId in salesPersonIds) {
                                    database.child("salespersons").child(salesPersonId).child("assignedGroupId").setValue(groupId)
                                    database.child("salespersons").child(salesPersonId).child("salesHeadId").setValue(salesHeadId)
                                }
                                Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                                    "Group created successfully.",
                                    Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                                    "Failed to create group: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SalesHeadCreateSalesPersonTeam,
                        "Failed to check existing groups: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Failed to generate group ID.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isValidFirebaseKey(key: String): Boolean {
        return key.matches(Regex("^[a-zA-Z0-9_-]*$"))
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
