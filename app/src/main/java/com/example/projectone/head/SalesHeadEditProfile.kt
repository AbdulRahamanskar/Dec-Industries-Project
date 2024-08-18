package com.example.projectone.head

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
//import com.example.projectone.admin.Admin
import com.example.projectone.head.salesheaddatamodels.SalesHead
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SalesHeadEditProfile : AppCompatActivity() {
    private lateinit var toolbarSalesheadEditProfile:Toolbar
    private lateinit var salesHeadEditProfileImage:ShapeableImageView
    private lateinit var salesHeadEditProfileFullName:TextInputEditText
    private lateinit var salesHeadEditProfileEmail:TextInputEditText
    private lateinit var salesHeadEditProfileMobileNo:TextInputEditText
    private lateinit var salesHeadEditProfileLinkedIn:TextInputEditText
    private lateinit var salesHeadEditProfileCompanyName:TextInputEditText
    private lateinit var salesHeadEditProfileCompanyAddress:TextInputEditText
    private lateinit var salesHeadEditProfileExperience:TextInputEditText
    private lateinit var salesHeadEditProfileHighestQualification:TextInputEditText
    private lateinit var salesHeadEditProfileLanguagueKnown:TextInputEditText
    private lateinit var salesHeadEditProfileSkills:TextInputEditText
    private lateinit var salesHeadEditProfileRecentprojectsHandle:TextInputEditText
    private lateinit var salesHeadEditProfileKeyClients:TextInputEditText
    private lateinit var salesHeadEditProfileUpdateBtn:Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                uploadImageToFirebase(imageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_edit_profile)
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        toolbarSalesheadEditProfile=findViewById(R.id.toolbar_saleshead_edit_profile)
        salesHeadEditProfileImage=findViewById(R.id.salesHeadEditProfileImage)
        salesHeadEditProfileFullName=findViewById(R.id.etEditSalesHeadFullName)
        salesHeadEditProfileEmail=findViewById(R.id.etEditSalesHeadEmail)
        salesHeadEditProfileMobileNo=findViewById(R.id.etEditSalesHeadMobileNumber)
        salesHeadEditProfileLinkedIn=findViewById(R.id.etEditSalesHeadLinkedIn)
        salesHeadEditProfileCompanyName=findViewById(R.id.etEditSalesHeadCompanyName)
        salesHeadEditProfileCompanyAddress=findViewById(R.id.etEditSalesHeadCompanyAddress)
        salesHeadEditProfileExperience=findViewById(R.id.etEditSalesHeadExperience)
        salesHeadEditProfileHighestQualification=findViewById(R.id.etEditSalesHeadHighestQualification)
        salesHeadEditProfileLanguagueKnown=findViewById(R.id.etEditSalesHeadLanguaguesKnown)
        salesHeadEditProfileSkills=findViewById(R.id.etEditSalesHeadSkills)
        salesHeadEditProfileRecentprojectsHandle=findViewById(R.id.etEditSalesHeadRecentProjectsHandle)
        salesHeadEditProfileKeyClients=findViewById(R.id.etEditSalesHeadKeyClients)
        salesHeadEditProfileUpdateBtn=findViewById(R.id.salesHeadEditProfileUpdateBtn)
        setSupportActionBar(toolbarSalesheadEditProfile)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Edit Profile"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Load existing profile data
        loadProfileData()

        // Set click listener for profile image
        salesHeadEditProfileImage.setOnClickListener {
            openGallery()
        }

        // Set click listener for update button
        salesHeadEditProfileUpdateBtn.setOnClickListener {
            updateProfileData()
        }


    }


    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val salesHeadData = snapshot.getValue(SalesHead::class.java)
            if (salesHeadData != null) {
                salesHeadEditProfileFullName.setText(salesHeadData.fullname)
                salesHeadEditProfileEmail.setText(salesHeadData.email)
                salesHeadEditProfileMobileNo.setText(salesHeadData.mobile)
                salesHeadEditProfileLinkedIn.setText(salesHeadData.linkedIn)
                salesHeadEditProfileCompanyName.setText(salesHeadData.companyName)
                salesHeadEditProfileCompanyAddress.setText(salesHeadData.companyAddress)
                salesHeadEditProfileExperience.setText(salesHeadData.experience)
                salesHeadEditProfileHighestQualification.setText(salesHeadData.highestQualification)
                salesHeadEditProfileLanguagueKnown.setText(salesHeadData.languageKnown)
                salesHeadEditProfileSkills.setText(salesHeadData.skills)
                salesHeadEditProfileRecentprojectsHandle.setText(salesHeadData.recentProjectsHandle)
                salesHeadEditProfileKeyClients.setText(salesHeadData.keyClientsHandled)

                // Load profile image
                val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
                val profileImageRef = storageRef.child(imagePath)

                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_internet_connection)
                        .into(salesHeadEditProfileImage)
                }.addOnFailureListener {
                    salesHeadEditProfileImage.setImageResource(R.drawable.sample_logo)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
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
                // Update UI to reflect the new photo
                loadProfileData()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val updatedData = mapOf(
            "fullname" to salesHeadEditProfileFullName.text.toString(),
            "email" to  salesHeadEditProfileEmail.text.toString(),
            "mobile" to salesHeadEditProfileMobileNo.text.toString(),
            "linkedIn" to  salesHeadEditProfileLinkedIn.text.toString(),
            "companyName" to salesHeadEditProfileCompanyName.text.toString(),
            "companyAddress" to salesHeadEditProfileCompanyAddress.text.toString(),
            "experience" to salesHeadEditProfileExperience.text.toString(),
            "highestQualification" to salesHeadEditProfileHighestQualification.text.toString(),
            "languageKnown" to salesHeadEditProfileLanguagueKnown.text.toString(),
            "skills" to salesHeadEditProfileSkills.text.toString(),
            "recentProjectsHandle" to salesHeadEditProfileRecentprojectsHandle.text.toString(),
            "keyClientsHandled" to salesHeadEditProfileKeyClients.text.toString()
        )

        database.child("users").child(userId).updateChildren(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("UpdateProfile", "Failed to update profile", e)
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }


//    private fun updateProfileData() {
//        val userId = auth.currentUser?.uid ?: return
//        val updatedData = SalesHead(
//            fullname = salesHeadEditProfileFullName.text.toString(),
//            email = salesHeadEditProfileEmail.text.toString(),
//            mobile = salesHeadEditProfileMobileNo.text.toString(),
//            linkedIn = salesHeadEditProfileLinkedIn.text.toString(),
//            companyName = salesHeadEditProfileCompanyName.text.toString(),
//            companyAddress = salesHeadEditProfileCompanyAddress.text.toString(),
//            experience = salesHeadEditProfileExperience.text.toString(),
//            highestQualification = salesHeadEditProfileHighestQualification.text.toString(),
//            languageKnown = salesHeadEditProfileLanguagueKnown.text.toString(),
//            skills = salesHeadEditProfileSkills.text.toString(),
//            recentProjectsHandle= salesHeadEditProfileRecentprojectsHandle.text.toString(),
//              keyClientsHandled= salesHeadEditProfileKeyClients.text.toString()
//        )
//
//        database.child("users").child(userId).setValue(updatedData)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
//
//            }
//    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}