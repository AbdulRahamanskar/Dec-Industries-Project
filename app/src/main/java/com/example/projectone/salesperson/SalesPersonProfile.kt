package com.example.projectone.salesperson

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.ImagePreviewActivity
import com.example.projectone.R
//import com.example.projectone.client.Client
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SalesPersonProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var salesPersonProfileImage:ShapeableImageView
    private lateinit var salesPersonProfileFullName:TextView
    private lateinit var salesPersonProfileRole:TextView
    private lateinit var salesPersonProfileEmail:TextView
    private lateinit var salesPersonProfileMobileNo:TextView
    private lateinit var salesPersonProfileEmpId:TextView
    private lateinit var salesPersonProfileAssignedHead:TextView
    private lateinit var salesPersonProfileCompanyName:TextView
    private lateinit var salesPersonProfileCompanyAddress:TextView
    private lateinit var salesPersonProfileExperience:TextView
    private lateinit var salesPersonProfileHighestQualifications:TextView
    private lateinit var salesPersonProfileSkills:TextView
    private lateinit var salesPersonProfileLanguageKnown:TextView
    private lateinit var salesPersonProfileStatus:TextView
    private lateinit var salesPersonProfileLinkedIn:TextView
    private lateinit var salesPersonProfileDoj:TextView
    private lateinit var salesPersonProfileRatings:TextView
    private lateinit var salesPersonProfileSalesTargetCompleted:TextView
    private lateinit var salesPersonBottomProfile:BottomNavigationView


    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_profile)
        //views
        salesPersonProfileImage=findViewById(R.id.salesPersonProfileImage)
        salesPersonProfileMobileNo=findViewById(R.id.salesPersonProfileMobileNumber)
        salesPersonProfileFullName=findViewById(R.id.salesPersonProfileName)
        salesPersonProfileRole=findViewById(R.id.salesPersonProfileRole)
        salesPersonProfileEmail=findViewById(R.id.salesPersonProfileEmail)
        salesPersonProfileEmpId=findViewById(R.id.salesPersonProfileEmpId)
        salesPersonProfileAssignedHead=findViewById(R.id.salesPersonProfileAssignedHead)
        salesPersonProfileCompanyName=findViewById(R.id.salesPersonProfileCompany)
        salesPersonProfileCompanyAddress=findViewById(R.id.salesPersonProfileCompanyRegion)
        salesPersonProfileExperience=findViewById(R.id.salesPersonProfileExperience)
        salesPersonProfileHighestQualifications=findViewById(R.id.salesPersonProfileHighestQualification)
        salesPersonProfileSkills=findViewById(R.id.salesPersonProfileSkills)
        salesPersonProfileStatus=findViewById(R.id.salesPersonProfileStatus)
        salesPersonProfileLanguageKnown=findViewById(R.id.salesPersonProfileLanguageKnown)
        salesPersonProfileDoj=findViewById(R.id.salesPersonProfileDoj)
        salesPersonProfileLinkedIn=findViewById(R.id.salesPersonProfileLinkedIn)
        salesPersonProfileSalesTargetCompleted=findViewById(R.id.salesPersonProfileSalesTargetCompleted)
        salesPersonProfileRatings=findViewById(R.id.salesPersonProfileRatings)

        //init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val imageBtnSalesPersonProfileCall=findViewById<ImageButton>(R.id.imageBtnSalesPersonProfileCall)
        val imageBtnSalesPersonProfileEmail=findViewById<ImageButton>(R.id.imageBtnSalesPersonProfileEmail)
        val imageBtnSalesPersonProfileWhatsApp=findViewById<ImageButton>(R.id.imageBtnSalesPersonProfileWhatsApp)
        val imageBtnSalesPersonProfileLinkedIn=findViewById<ImageButton>(R.id.imageBtnSalesPersonProfileLinkedIn)
        imageBtnSalesPersonProfileCall.setOnClickListener {
            Toast.makeText(this, "Call Clicked...", Toast.LENGTH_SHORT).show()
        }
        imageBtnSalesPersonProfileEmail.setOnClickListener {
            Toast.makeText(this, "Email Clicked...", Toast.LENGTH_SHORT).show()
        }
        imageBtnSalesPersonProfileWhatsApp.setOnClickListener {
            Toast.makeText(this, "Whats app Clicked...", Toast.LENGTH_SHORT).show()
        }
        imageBtnSalesPersonProfileLinkedIn.setOnClickListener {
            Toast.makeText(this, "LinkedIn Clicked...", Toast.LENGTH_SHORT).show()
        }

        updateSalesPersonRating(auth.currentUser?.uid ?: "unknown")

        //load data
        loadsalesPersonProfileData()
        //marquee effect for textview
        marqueeEffect()
        updateSalesPersonRating("empId")

        salesPersonProfileImage.setOnLongClickListener {
            openGallery()
            true
        }

        // Image preview on click
        salesPersonProfileImage.setOnClickListener {
            val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
            val profileImageRef = storageRef.child(imagePath)

            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                if (!isDestroyed) {
                    val intent = Intent(this, ImagePreviewActivity::class.java).apply {
                        putExtra("IMAGE_URL", uri.toString())
                    }
                    startActivity(intent)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }



        // Retrieve status from SharedPreferences
        val sharedPreferences = getSharedPreferences("SalesPersonPrefs", MODE_PRIVATE)
        val status = sharedPreferences.getString("STATUS", "Offline") // Default to "Offline" if not set
        updateProfileStatus(status ?: "Offline")


        //fab edit profile

        val fabEditProfileSalesProerson=findViewById<FloatingActionButton>(R.id.salesPersonEditProfileFAB)

        fabEditProfileSalesProerson.setOnClickListener {
            startActivity(Intent(this,SalesPersonEditProfile::class.java))
        }

        val toolbarSalesPersonProfile=findViewById<Toolbar>(R.id.toolbarSalesPersonProfile)
        setSupportActionBar(toolbarSalesPersonProfile)
        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle="Profile"
        }
        salesPersonBottomProfile=findViewById(R.id.salesPersonBottom)
        salesPersonBottomProfile.selectedItemId=R.id.salesPersonProfileMenu

        salesPersonBottomProfile.setOnNavigationItemSelectedListener {
            navigateToScreen(it.itemId)
        }





    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }
    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val imagePath = "profile_images/$userId.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()

                // Get the download URL of the uploaded image
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userRef = database.child("users").child(userId)
                    // Store the image URL in the Realtime Database
                    userRef.child("profileImageUrl").setValue(uri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Image URL saved to database: ", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    // Update UI to reflect the new photo
                    loadsalesPersonProfileData()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }




    private fun loadsalesPersonProfileData() {
        val userId=auth.currentUser?.uid?:return
        val userRef= database.child("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val salesPersonData=snapshot.getValue(SalesPerson::class.java)
                    if (salesPersonData!=null){
                        Log.d("SalesPersonProfile", "Ratings: ${salesPersonData.ratings}")

                        populateSalesPersonData(salesPersonData)
                    }else{
                        Toast.makeText(this@SalesPersonProfile, "Sales Person data not found", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this@SalesPersonProfile, "No data available", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesPersonProfile, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun populateSalesPersonData(salesPersonData: SalesPerson) {
        salesPersonProfileFullName.text=salesPersonData.fullname
        salesPersonProfileEmail.text=salesPersonData.email
        salesPersonProfileMobileNo.text=salesPersonData.mobile
        salesPersonProfileEmpId.text=salesPersonData.empId
        salesPersonProfileRole.text=salesPersonData.role
        salesPersonProfileLinkedIn.text=salesPersonData.linkedIn
        salesPersonProfileCompanyName.text=salesPersonData.companyName
        salesPersonProfileCompanyAddress.text=salesPersonData.companyAddress
        salesPersonProfileDoj.text=salesPersonData.doj
        salesPersonProfileHighestQualifications.text=salesPersonData.highestQualification
        salesPersonProfileSkills.text=salesPersonData.skills
        salesPersonProfileExperience.text=salesPersonData.experience
        salesPersonProfileLanguageKnown.text=salesPersonData.languageKnown
        salesPersonProfileSalesTargetCompleted.text=salesPersonData.salesTargetsCompleted
        salesPersonProfileRatings.text=salesPersonData.ratings?:"0.0"

        Log.d("SalesTargetCompleted","${salesPersonData.salesTargetsCompleted}")

        // Load profile image from Firebase Storage
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            if (!isDestroyed) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.no_internet_connection) // Error image if loading fails
                    .into(salesPersonProfileImage)
            }
        }.addOnFailureListener {
            if (!isDestroyed) {
                // Handle any errors
                salesPersonProfileImage.setImageResource(R.drawable.sample_logo) // Default image
            }
        }

        // Load SalesHead information
        val assignedGroupId = salesPersonData.assignedGroupId
        if (assignedGroupId != null) {
            Log.d("SalesPersonProfile", "Assigned Group ID: $assignedGroupId")
            val groupRef = database.child("groups").child(assignedGroupId)
            groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(groupSnapshot: DataSnapshot) {
                    val salesHeadId = groupSnapshot.child("salesHeadId").getValue(String::class.java)
                    Log.d("SalesPersonProfile", "SalesHead ID: $salesHeadId")
                    if (salesHeadId != null) {
                        val salesHeadRef = database.child("users").child(salesHeadId)
                        salesHeadRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(salesHeadSnapshot: DataSnapshot) {
                                val salesHeadEmpId = salesHeadSnapshot.child("empId").getValue(String::class.java) ?: "Unknown"
                                salesPersonProfileAssignedHead.text = salesHeadEmpId
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(this@SalesPersonProfile, "Failed to load SalesHead data", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        salesPersonProfileAssignedHead.text = "No SalesHead assigned"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@SalesPersonProfile, "Failed to load group data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            salesPersonProfileAssignedHead.text = "No group assigned"
        }

    }

    private fun updateSalesPersonRating(empId: String) {
        database.child("salesPersonRatings").child(empId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val totalRatings = snapshot.childrenCount
                    var ratingSum = 0.0

                    for (ratingSnapshot in snapshot.children) {
                        val rating = ratingSnapshot.child("rating").getValue(Double::class.java) ?: 0.0
                        ratingSum += rating
                    }

                    val averageRating = if (totalRatings > 0) ratingSum / totalRatings else 0.0
                    salesPersonProfileRatings.text = String.format("%.1f", averageRating)
                } else {
                    salesPersonProfileRatings.text = "0.0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesPersonProfile, "Failed to load ratings: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }








    private fun marqueeEffect(){
        salesPersonProfileStatus.isSelected=true
        salesPersonProfileEmail.isSelected=true
        salesPersonProfileFullName.isSelected=true
        salesPersonProfileCompanyName.isSelected=true
        salesPersonProfileCompanyAddress.isSelected=true
        salesPersonProfileHighestQualifications.isSelected=true
        salesPersonProfileSkills.isSelected=true
        salesPersonProfileLanguageKnown.isSelected=true
        salesPersonProfileExperience.isSelected=true
        salesPersonProfileLinkedIn.isSelected=true
        salesPersonProfileAssignedHead.isSelected=true
    }

    // Function to update the profile status in the UI
    private fun updateProfileStatus(status: String) {
        val statusTextView = findViewById<TextView>(R.id.salesPersonProfileStatus)
        statusTextView.text = status

        when (status) {
            "Online" -> {
                statusTextView.setTextColor(getColor(R.color.green)) // Set color to green
            }
            "Offline" -> {
                statusTextView.setTextColor(getColor(R.color.red)) // Set color to red
            }
            "BRB"-> {
                statusTextView.setTextColor(getColor(R.color.blue)) // Set color to blue
            }
            "Break"->{
                statusTextView.setTextColor(getColor(R.color.light_black)) // Set color to gray

            }
        }
    }

    private fun navigateToScreen(itemId: Int):Boolean {
        if (itemId==salesPersonBottomProfile.selectedItemId){
            return true
        }
        when(itemId){
            R.id.salesPersonProfileMenu->{
                return true
            }
            R.id.salesPersonProjectsMenu->{
                startActivity(Intent(this,SalesPersonProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonCreateOrderMenu->{
                startActivity(Intent(this,SalesPersonCreateOrder::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonProfileMenu->{
                startActivity(Intent(this,SalesPersonProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonSettingsMenu->{
                startActivity(Intent(this,SalesPersonMoreSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
        }
        }

    }
data class ClientFeedback(
    val salesPersonId: String = "",
    val rating: Float = 0f
)

