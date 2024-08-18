package com.example.projectone.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.admin.admindatamodels.Admin
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class AdminEditProfile : AppCompatActivity() {

    private lateinit var adminProfileEditProfileImage: CircleImageView
    private lateinit var adminProfileEditFullName: TextInputEditText
    private lateinit var adminProfileEditEmail: TextInputEditText
    private lateinit var adminProfileMobileNumber: TextInputEditText
    private lateinit var adminProfileEditLinkedIn: TextInputEditText
    private lateinit var adminProfileEditCompanyName: TextInputEditText
    private lateinit var adminProfileEditCompanyAddress: TextInputEditText
    private lateinit var adminProfileEditExperience: TextInputEditText
    private lateinit var adminProfileEditHighestQualification: TextInputEditText
    private lateinit var adminProfileEditiLanguageKnown: TextInputEditText
    private lateinit var adminProfileEditSkills: TextInputEditText
    private lateinit var adminProfileEditUpdateButton: Button

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
        setContentView(R.layout.activity_admin_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Initialize views
        adminProfileEditProfileImage = findViewById(R.id.adminProfileEditimage)
        adminProfileEditFullName = findViewById(R.id.etEditAdminFullName)
        adminProfileEditEmail = findViewById(R.id.etEditAdminEmail)
        adminProfileMobileNumber = findViewById(R.id.etEditAdminMobileNumber)
        adminProfileEditLinkedIn = findViewById(R.id.etEditAdminLinkedIn)
        adminProfileEditCompanyName = findViewById(R.id.etEditAdminCompanyName)
        adminProfileEditCompanyAddress = findViewById(R.id.etEditAdminCompanyRegion)
        adminProfileEditExperience = findViewById(R.id.etEditAdminExperience)
        adminProfileEditHighestQualification = findViewById(R.id.etEditAdminHighestQualification)
        adminProfileEditiLanguageKnown = findViewById(R.id.etEditAdminLanguaguesKnown)
        adminProfileEditSkills = findViewById(R.id.etEditAdminSkills)
        adminProfileEditUpdateButton = findViewById(R.id.btnAdminProfileditUpdate)
        val toolbarAdminEditProfile = findViewById<Toolbar>(R.id.toolbar_admin_edit_profile)

        setSupportActionBar(toolbarAdminEditProfile)
        supportActionBar?.apply {
            title = ""
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }

        // Load existing profile data
        loadProfileData()

        // Set click listener for profile image
        adminProfileEditProfileImage.setOnClickListener {
            openGallery()
        }

        // Set click listener for update button
        adminProfileEditUpdateButton.setOnClickListener {
            updateProfileData()
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val adminData = snapshot.getValue(Admin::class.java)
            if (adminData != null) {
                adminProfileEditFullName.setText(adminData.fullname)
                adminProfileEditEmail.setText(adminData.email)
                adminProfileMobileNumber.setText(adminData.mobile)
                adminProfileEditLinkedIn.setText(adminData.linkedIn)
                adminProfileEditCompanyName.setText(adminData.companyName)
                adminProfileEditCompanyAddress.setText(adminData.companyAddress)
                adminProfileEditExperience.setText(adminData.experience)
                adminProfileEditHighestQualification.setText(adminData.highestQualification)
                adminProfileEditiLanguageKnown.setText(adminData.languagesKnown)
                adminProfileEditSkills.setText(adminData.skills)

                // Load profile image
                val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
                val profileImageRef = storageRef.child(imagePath)

                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_internet_connection)
                        .into(adminProfileEditProfileImage)
                }.addOnFailureListener {
                    adminProfileEditProfileImage.setImageResource(R.drawable.sample_logo)
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
            "fullname" to adminProfileEditFullName.text.toString(),
            "email" to adminProfileEditEmail.text.toString(),
            "mobile" to adminProfileMobileNumber.text.toString(),
            "linkedIn" to adminProfileEditLinkedIn.text.toString(),
            "companyName" to adminProfileEditCompanyName.text.toString(),
            "companyAddress" to adminProfileEditCompanyAddress.text.toString(),
            "experience" to adminProfileEditExperience.text.toString(),
            "highestQualification" to adminProfileEditHighestQualification.text.toString(),
            "languagesKnown" to adminProfileEditiLanguageKnown.text.toString(),
            "skills" to adminProfileEditSkills.text.toString()
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
//        val updatedData = Admin(
//            fullname = adminProfileEditFullName.text.toString(),
//            email = adminProfileEditEmail.text.toString(),
//            mobile = adminProfileMobileNumber.text.toString(),
//            linkedIn = adminProfileEditLinkedIn.text.toString(),
//            companyName = adminProfileEditCompanyName.text.toString(),
//            companyAddress = adminProfileEditCompanyAddress.text.toString(),
//            experience = adminProfileEditExperience.text.toString(),
//            highestQualification = adminProfileEditHighestQualification.text.toString(),
//            languagesKnown = adminProfileEditiLanguageKnown.text.toString(),
//            skills = adminProfileEditSkills.text.toString()
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
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
