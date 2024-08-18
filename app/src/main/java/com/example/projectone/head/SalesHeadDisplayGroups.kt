package com.example.projectone.head

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectone.R
import com.example.projectone.head.adapters.GroupAdapter
import com.example.projectone.head.salesheaddatamodels.Group
import com.google.firebase.database.*

class SalesHeadDisplayGroups : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var groupAdapter: GroupAdapter
    private val groups = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_display_groups)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("groups")

        // Retrieve data from Firebase
        retrieveGroupsFromFirebase()
    }

    private fun retrieveGroupsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groups.clear()
                for (groupSnapshot in snapshot.children) {
                    Log.d("Firebase", "Snapshot: ${groupSnapshot.value}")
                    val group = groupSnapshot.getValue(Group::class.java)
                    if (group != null) {
                        Log.d("Firebase", "Group Name: ${group.name}") // Debug log
                        Log.d("Firebase", "Sales Head Name: ${group.salesHeadName}") // Debug log
                        Log.d("Firebase", "Sales Head ID: ${group.salesHeadId}") // Debug log
                        groups.add(group)
                    }
                }
                updateGroupList()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesHeadDisplayGroups, "Failed to retrieve groups: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateGroupList() {
        val listViewGroups: ListView = findViewById(R.id.listViewGroups)
        groupAdapter = GroupAdapter(this, groups)
        listViewGroups.adapter = groupAdapter

        listViewGroups.setOnItemClickListener { _, _, position, _ ->
            val group = groups[position]
            showGroupOptions(group)
        }
    }


    private fun showGroupOptions(group: Group) {
        // Implement options to remove salesperson or delete the group
        // For now, show a Toast message as a placeholder
        Toast.makeText(this, "Group selected: ${group.salesHeadName}", Toast.LENGTH_SHORT).show()
    }

    }


