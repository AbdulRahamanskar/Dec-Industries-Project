package com.example.projectone.salesperson

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
//import com.example.projectone.admin.Admin
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SalesPersonEditProfile : AppCompatActivity() {
    private lateinit var toolbarSalespersonEditProfile:Toolbar
    private lateinit var salesPersonEditProfileImage:ShapeableImageView
    private lateinit var salesPersonEditProfileFullName:TextInputEditText
    private lateinit var salesPersonEditProfileEmail:TextInputEditText
    private lateinit var salesPersonEditProfileMobileNo:TextInputEditText
    private lateinit var salesPersonEditProfileLinkedIn:TextInputEditText
    private lateinit var salesPersonEditProfileCompanyName:TextInputEditText
    private lateinit var salesPersonEditProfileCompanyAddress:TextInputEditText
    private lateinit var salesPersonEditProfileExperience:TextInputEditText
    private lateinit var salesPersonEditProfileHighestQualification:TextInputEditText
    private lateinit var salesPersonEditProfileLanguagesKnown:TextInputEditText
    private lateinit var salesPersonEditProfileSkills:TextInputEditText
    private lateinit var salesPersonUpdateProfile:Button

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
        setContentView(R.layout.activity_sales_person_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        toolbarSalespersonEditProfile=findViewById(R.id.toolbar_salesperson_edit_profile)
        salesPersonEditProfileImage=findViewById(R.id.salesPersonEditProfileImage)
        salesPersonEditProfileFullName=findViewById(R.id.etEditSalesPersonFullName)
        salesPersonEditProfileEmail=findViewById(R.id.etEditSalesPersonEmail)
        salesPersonEditProfileMobileNo=findViewById(R.id.etEditSalesPersonMobileNumber)
        salesPersonEditProfileLinkedIn=findViewById(R.id.etEditSalesPersonsLinkedIn)
        salesPersonEditProfileCompanyName=findViewById(R.id.etEditSalesPersonCompanyName)
        salesPersonEditProfileCompanyAddress=findViewById(R.id.etEditSalesPersonCompanyRegion)
        salesPersonEditProfileExperience=findViewById(R.id.etEditSalesPersonExperience)
        salesPersonEditProfileHighestQualification=findViewById(R.id.etEditSalesPersonHighestQualification)
        salesPersonEditProfileLanguagesKnown=findViewById(R.id.etEditSalesPersonLanguaguesKnown)
        salesPersonEditProfileSkills=findViewById(R.id.etEditSalesPersonSkills)
        salesPersonUpdateProfile=findViewById(R.id.salesPersonUpdateProfile)
        setSupportActionBar(toolbarSalespersonEditProfile)
        supportActionBar?.apply {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.subtitle="Edit Profile"
            supportActionBar?.title=""
        }


        // Load existing profile data
        loadProfileData()

        // Set click listener for profile image
        salesPersonEditProfileImage.setOnClickListener {
            openGallery()
        }

        // Set click listener for update button
        salesPersonUpdateProfile.setOnClickListener {
            updateProfileData()
        }
    }


    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val salesPersonData = snapshot.getValue(SalesPerson::class.java)
            if (salesPersonData != null) {
                salesPersonEditProfileFullName.setText(salesPersonData.fullname)
                salesPersonEditProfileEmail.setText(salesPersonData.email)
                salesPersonEditProfileMobileNo.setText(salesPersonData.mobile)
                salesPersonEditProfileLinkedIn.setText(salesPersonData.linkedIn)
                salesPersonEditProfileCompanyName.setText(salesPersonData.companyName)
                salesPersonEditProfileCompanyAddress.setText(salesPersonData.companyAddress)
                salesPersonEditProfileExperience.setText(salesPersonData.experience)
                salesPersonEditProfileHighestQualification.setText(salesPersonData.highestQualification)
                salesPersonEditProfileLanguagesKnown.setText(salesPersonData.languageKnown)
                salesPersonEditProfileSkills.setText(salesPersonData.skills)

                // Load profile image
                val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
                val profileImageRef = storageRef.child(imagePath)

                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_internet_connection)
                        .into(salesPersonEditProfileImage)
                }.addOnFailureListener {
                    salesPersonEditProfileImage.setImageResource(R.drawable.sample_logo)
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

        // Create a map of only the fields you want to update
        val updatedData = mapOf(
            "fullname" to salesPersonEditProfileFullName.text.toString(),
            "email" to salesPersonEditProfileEmail.text.toString(),
            "mobile" to salesPersonEditProfileMobileNo.text.toString(),
            "linkedIn" to salesPersonEditProfileLinkedIn.text.toString(),
            "companyName" to salesPersonEditProfileCompanyName.text.toString(),
            "companyAddress" to salesPersonEditProfileCompanyAddress.text.toString(),
            "experience" to salesPersonEditProfileExperience.text.toString(),
            "highestQualification" to salesPersonEditProfileHighestQualification.text.toString(),
            "languageKnown" to salesPersonEditProfileLanguagesKnown.text.toString(),
            "skills" to salesPersonEditProfileSkills.text.toString()
        )

        // Update only the specified fields
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
//        val updatedData = SalesPerson(
//            fullname = salesPersonEditProfileFullName.text.toString(),
//            email = salesPersonEditProfileEmail.text.toString(),
//            mobile = salesPersonEditProfileMobileNo.text.toString(),
//            linkedIn = salesPersonEditProfileLinkedIn.text.toString(),
//            companyName = salesPersonEditProfileCompanyName.text.toString(),
//            companyAddress = salesPersonEditProfileCompanyAddress.text.toString(),
//            experience = salesPersonEditProfileExperience.text.toString(),
//            highestQualification = salesPersonEditProfileHighestQualification.text.toString(),
//            languageKnown = salesPersonEditProfileLanguagesKnown.text.toString(),
//            skills = salesPersonEditProfileSkills.text.toString()
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
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}