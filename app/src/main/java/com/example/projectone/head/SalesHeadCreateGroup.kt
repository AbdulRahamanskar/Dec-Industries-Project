package com.example.projectone.head

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class SalesHeadCreateGroup : AppCompatActivity() {
    private lateinit var uploadGroupImage: CircleImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var groupTitle: TextInputEditText
    private lateinit var buttonSaveGroup: Button
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var groupImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedSalesPersonIds: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_create_group)

        progressBar = findViewById(R.id.createGroupLodingBar)
        buttonSaveGroup = findViewById(R.id.buttonSaveGroup)
        groupTitle = findViewById(R.id.etGroupName)
        uploadGroupImage = findViewById(R.id.cvGroupProfile)
        val toolbarSalesHeadCreateGroup = findViewById<Toolbar>(R.id.toolbarSalesHeadCreateGroup)
        setSupportActionBar(toolbarSalesHeadCreateGroup)
        supportActionBar?.apply {
            title = ""
            subtitle = "Create Group"
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        selectedSalesPersonIds = intent.getStringArrayListExtra("selectedSalesPersonIds") ?: emptyList()

        uploadGroupImage.setOnClickListener {
            openImagePicker()
        }

        buttonSaveGroup.setOnClickListener {
            val name = groupTitle.text.toString().trim()
            if (groupImageUri != null && name.isNotEmpty()) {
                uploadGroupImageToFirebase(groupImageUri!!) { imageUrl ->
                    getCurrentSalesHeadId { salesHeadId ->
                        if (salesHeadId != null) {
                            createGroup(name, imageUrl, salesHeadId)
                        } else {
                            Toast.makeText(this, "Failed to get SalesHead ID", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please provide both image and group name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showProgressBar() {
        runOnUiThread {
            progressBar.visibility = ProgressBar.VISIBLE
        }
    }

    private fun hideProgressBar() {
        runOnUiThread {
            progressBar.visibility = ProgressBar.GONE
        }
    }



    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            groupImageUri = data.data
            groupImageUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    uploadGroupImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.e("SalesHeadCreateGroup", "Error loading image", e)
                }
            }
        }
    }

    private fun uploadGroupImageToFirebase(uri: Uri, callback: (String) -> Unit) {
       showProgressBar()
        val storageReference = storage.reference.child("group_images/${System.currentTimeMillis()}.jpg")
        storageReference.putFile(uri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    hideProgressBar()
                    callback(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                hideProgressBar()
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("SalesHeadCreateGroup", "Image upload failed", e)
            }
    }

    private fun getCurrentSalesHeadId(callback: (String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val userId = it.uid
            val userRef = database.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userRole = dataSnapshot.child("role").getValue(String::class.java)
                    if (userRole == "saleshead") {
                        callback(userId)
                    } else {
                        Log.e("SalesHeadCreateGroup", "Current user is not a SalesHead")
                        callback(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SalesHeadCreateGroup", "Failed to retrieve user data", databaseError.toException())
                    callback(null)
                }
            })
        } ?: run {
            Log.e("SalesHeadCreateGroup", "No authenticated user found")
            callback(null)
        }
    }
    private fun createGroup(name: String, imageUrl: String, salesHeadId: String) {
        showProgressBar()
        val groupId = database.child("groups").push().key
        groupId?.let {
            // Fetch sales head details
            val salesHeadRef = database.child("users").child(salesHeadId)
            salesHeadRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(salesHeadSnapshot: DataSnapshot) {
                    val salesHeadName = salesHeadSnapshot.child("fullname").getValue(String::class.java) ?: "Unknown"
                    val salesHeadEmpId = salesHeadSnapshot.child("empId").getValue(String::class.java) ?: "Unknown"

                    // Check if any selected salesperson is already in a group
                    val salesPersonsRef = database.child("users")
                    val salesPersonsCheck = selectedSalesPersonIds.map { salesPersonId ->
                        salesPersonsRef.child(salesPersonId).child("assignedGroupId").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val assignedGroupId = task.result?.getValue(String::class.java)
                                if (assignedGroupId != null && assignedGroupId != groupId) {
                                    // SalesPerson is already assigned to another group
                                    hideProgressBar()
                                    Toast.makeText(this@SalesHeadCreateGroup, "Salesperson $salesPersonId is already assigned to another group.", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Proceed with adding to the new group
                                    if (selectedSalesPersonIds.indexOf(salesPersonId) == selectedSalesPersonIds.size - 1) {
                                        val groupDetails = mapOf(
                                            "name" to name,
                                            "imageUrl" to imageUrl,
                                            "salesHeadId" to salesHeadEmpId,
                                            "salesHeadName" to salesHeadName
                                        )

                                        database.child("groups").child(groupId).setValue(groupDetails)
                                            .addOnSuccessListener {
                                                // Update selected salespersons
                                                selectedSalesPersonIds.forEach { salesPersonId ->
                                                    database.child("users").child(salesPersonId).apply {
                                                        child("assignedGroupId").setValue(groupId)
                                                        child("salesHeadId").setValue(salesHeadId)
                                                    }
                                                }
                                                hideProgressBar()
                                                Toast.makeText(this@SalesHeadCreateGroup, "Group created successfully.", Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                hideProgressBar()
                                                Toast.makeText(this@SalesHeadCreateGroup, "Failed to create group: ${e.message}", Toast.LENGTH_SHORT).show()
                                                Log.e("SalesHeadCreateGroup", "Group creation failed", e)
                                            }
                                    }
                                }
                            } else {
                                // Error in reading data
                                hideProgressBar()
                                Toast.makeText(this@SalesHeadCreateGroup, "Failed to check salesperson assignment.", Toast.LENGTH_SHORT).show()
                                Log.e("SalesHeadCreateGroup", "Failed to check salesperson assignment", task.exception)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hideProgressBar()
                    Log.e("SalesHeadCreateGroup", "Failed to retrieve sales head data", databaseError.toException())
                    Toast.makeText(this@SalesHeadCreateGroup, "Failed to retrieve sales head data", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            hideProgressBar()
            Toast.makeText(this, "Failed to generate group ID.", Toast.LENGTH_SHORT).show()
        }
    }





//    private fun createGroup(name: String, imageUrl: String, salesHeadId: String) {
//        showProgressBar()
//        val groupId = database.child("groups").push().key
//        groupId?.let {
//            // Fetch sales head details
//            val salesHeadRef = database.child("users").child(salesHeadId)
//            salesHeadRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(salesHeadSnapshot: DataSnapshot) {
//                    val salesHeadName = salesHeadSnapshot.child("fullname").getValue(String::class.java) ?: "Unknown"
//                    val salesHeadEmpId = salesHeadSnapshot.child("empId").getValue(String::class.java) ?: "Unknown"
//
//                    // Check if any selected salesperson is already in a group
//                    val salesPersonsRef = database.child("users")
//                    val salesPersonsCheck = selectedSalesPersonIds.map { salesPersonId ->
//                        salesPersonsRef.child(salesPersonId).child("assignedGroupId").get().addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val assignedGroupId = task.result?.getValue(String::class.java)
//                                if (assignedGroupId != null) {
//                                    // SalesPerson is already assigned to a group
//                                    // Show error and exit
//                                    hideProgressBar()
//                                    Toast.makeText(this@SalesHeadCreateGroup, "Salesperson already assigned to a group.", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    // Proceed with adding to the new group
//                                    if (selectedSalesPersonIds.indexOf(salesPersonId) == selectedSalesPersonIds.size - 1) {
//                                        val groupDetails = mapOf(
//                                            "name" to name,
//                                            "imageUrl" to imageUrl,
//                                            "salesHeadId" to salesHeadEmpId,
//                                            "salesHeadName" to salesHeadName
//                                        )
//
//                                        database.child("groups").child(groupId).setValue(groupDetails)
//                                            .addOnSuccessListener {
//                                                // Update selected salespersons
//                                                selectedSalesPersonIds.forEach { salesPersonId ->
//                                                    database.child("users").child(salesPersonId).apply {
//                                                        child("assignedGroupId").setValue(groupId)
//                                                        child("salesHeadId").setValue(salesHeadId)
//                                                    }
//                                                }
//                                                hideProgressBar()
//                                                Toast.makeText(this@SalesHeadCreateGroup, "Group created successfully.", Toast.LENGTH_SHORT).show()
//                                                finish()
//                                            }
//                                            .addOnFailureListener { e ->
//                                                hideProgressBar()
//                                                Toast.makeText(this@SalesHeadCreateGroup, "Failed to create group: ${e.message}", Toast.LENGTH_SHORT).show()
//                                                Log.e("SalesHeadCreateGroup", "Group creation failed", e)
//                                            }
//                                    }
//                                }
//                            } else {
//                                // Error in reading data
//                                hideProgressBar()
//                                Toast.makeText(this@SalesHeadCreateGroup, "Failed to check salesperson assignment.", Toast.LENGTH_SHORT).show()
//                                Log.e("SalesHeadCreateGroup", "Failed to check salesperson assignment", task.exception)
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    hideProgressBar()
//                    Log.e("SalesHeadCreateGroup", "Failed to retrieve sales head data", databaseError.toException())
//                    Toast.makeText(this@SalesHeadCreateGroup, "Failed to retrieve sales head data", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } ?: run {
//            hideProgressBar()
//            Toast.makeText(this, "Failed to generate group ID.", Toast.LENGTH_SHORT).show()
//        }
//    }




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
