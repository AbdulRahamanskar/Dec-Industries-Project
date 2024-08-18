package com.example.projectone.client.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.client.clientdatamodels.Client
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ClientAdapter(
    context: Context,
    private val originalClientList: List<Client>
) : ArrayAdapter<Client>(context, 0, originalClientList), Filterable {

    private var filteredClientList = originalClientList
    private val clientFilter = ClientFilter()

    override fun getFilter(): Filter {
        return clientFilter
    }

    override fun getCount(): Int {
        return filteredClientList.size
    }

    override fun getItem(position: Int): Client? {
        return filteredClientList.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.adminclientuserslist, parent, false)

        val client = getItem(position)
        client?.let {
            val profileImageView = view.findViewById<ShapeableImageView>(R.id.adminClientProfileImage)
            val fullNameTextView = view.findViewById<TextView>(R.id.adminClientFullName)
            val roleTextView = view.findViewById<TextView>(R.id.adminClientRole)
            val clientIdTextView = view.findViewById<TextView>(R.id.adminClientId)
            val clientDojTextView = view.findViewById<TextView>(R.id.adminClientDoj)
            val clientEmailTextView = view.findViewById<TextView>(R.id.adminClientEmail)
            val clientDeleteUserImageButton = view.findViewById<ImageButton>(R.id.adminClientDeleteImageButton)

            fullNameTextView.text = "Name: ${it.fullname}".ifEmpty { "No Name" }
            roleTextView.text = "Role: ${it.role}".ifEmpty { "No Role" }
            clientIdTextView.text = "ClientId: ${it.clientId}".ifEmpty { "No Client ID" }
            clientDojTextView.text = "Joined: ${it.doj}".ifEmpty { "No Doj" }
            clientEmailTextView.text = "Email: ${it.email}".ifEmpty { "No Email" }

            clientDeleteUserImageButton.setOnClickListener {
                // Create an AlertDialog Builder
                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("Delete Account")
                builder.setMessage("Are you sure you want to delete this account? This action cannot be undone.")

                // Set the positive button
                builder.setPositiveButton("Yes") { dialog, _ ->
                    // Proceed with deletion
                    deleteUser(client)
                    dialog.dismiss()
                }

                // Set the negative button
                builder.setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }

                // Show the AlertDialog
                val alertDialog = builder.create()
                alertDialog.show()
            }




            // Load profile image
            Glide.get(context).clearMemory()
            if (it.profileImageUrl.isNotEmpty()) {

                Glide.with(context)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.sample_logo) // Placeholder image if URL is empty or loading
                    .error(R.drawable.sample_logo) // Error image if loading fails
                    .into(profileImageView as ImageView)
                // Debug log
                Log.d("ClientAdapter", "Loading image URL: ${it.profileImageUrl}")

            } else {
                // Debug log
                Log.d("ClientAdapter", "Loading image URL: ${it.profileImageUrl}")

                profileImageView.setImageResource(R.drawable.sample_logo) // Use a default image if URL is empty
            }
        }




        return view
    }



    private fun deleteUser(client: Client) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Admin re-authenticates
        currentUser?.let {
            val adminEmail = it.email ?: return
            Log.d("Client Adapter Delete","${it.email}")
            val adminPassword = "password" // Admin password should be securely obtained
            auth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Re-authenticate and delete the target user
                    auth.signInWithEmailAndPassword(client.email, client.password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.delete()?.addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    // User deleted from Firebase Authentication
                                    deleteUserFromDatabase(client.clientId)
                                } else {
                                    Toast.makeText(context, "Failed to delete user: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Failed to log in: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Admin re-authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteUserFromDatabase(clientId: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(clientId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "User data deleted from database.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private inner class ClientFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            val query = constraint?.toString()?.lowercase() ?: ""
            val filteredList = if (query.isEmpty()) {
                originalClientList
            } else {
                originalClientList.filter {
                    it.fullname.lowercase().contains(query) || it.clientId.lowercase().contains(query)
                }
            }

            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredClientList = results?.values as List<Client>? ?: emptyList()
            notifyDataSetChanged()
        }
    }
}

