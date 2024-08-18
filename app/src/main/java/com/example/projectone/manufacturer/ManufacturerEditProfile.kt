package com.example.projectone.manufacturer

import android.app.Activity
import android.content.Intent
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
import com.example.projectone.manufacturer.manufacturerdatamodels.Manufacturer
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ManufacturerEditProfile : AppCompatActivity() {
    private lateinit var manufacturerEditProfileImage:ShapeableImageView
    private lateinit var manufacturerEditProfileName:TextInputEditText
    private lateinit var manufacturerEditProfileEmail:TextInputEditText
    private lateinit var manufacturerEditProfileMobile:TextInputEditText
    private lateinit var manufacturerEditProfileLinkedIn:TextInputEditText
    private lateinit var manufacturerEditProfileCompanyName:TextInputEditText
    private lateinit var manufacturerEditProfileCompanyAddress:TextInputEditText
    private lateinit var manufacturerEditProfileExperience:TextInputEditText
    private lateinit var manufacturerEditProfileHighestQualification:TextInputEditText
    private lateinit var manufacturerEditProfileSkills:TextInputEditText
    private lateinit var manufacturerEditProfileLanguageKnown:TextInputEditText
    private lateinit var manufacturerEditProfileManPowerAvailable:TextInputEditText
    private lateinit var manufacturerEditProfileMachineResources:TextInputEditText
    private lateinit var manufacturerEditProfileBudgetEstTools:TextInputEditText
    private lateinit var updateManufacturerProfileButton:Button


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
        setContentView(R.layout.activity_manufacturer_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        manufacturerEditProfileImage=findViewById(R.id.manufacturerEditProfileImage)
        manufacturerEditProfileName=findViewById(R.id.etEditManufacurerFullName)
        manufacturerEditProfileEmail=findViewById(R.id.etEditManufacurerEmail)
        manufacturerEditProfileMobile=findViewById(R.id.etEditManufacurerMobileNumber)
        manufacturerEditProfileLinkedIn=findViewById(R.id.etEditManufacurerLinkedIn)
        manufacturerEditProfileCompanyName=findViewById(R.id.etEditManufacurerCompanyName)
        manufacturerEditProfileCompanyAddress=findViewById(R.id.etEditManufacurerCompanyRegion)
        manufacturerEditProfileExperience=findViewById(R.id.etEditManufacurerExperience)
        manufacturerEditProfileHighestQualification=findViewById(R.id.etEditManufacurerHighestQualification)
        manufacturerEditProfileSkills=findViewById(R.id.etEditManufacurerSkills)
        manufacturerEditProfileLanguageKnown=findViewById(R.id.etEditManufacurerLanguaguesKnown)
        manufacturerEditProfileManPowerAvailable=findViewById(R.id.etEditManufacurerManPowerAvailable)
        manufacturerEditProfileMachineResources=findViewById(R.id.etEditManufacurerMachineResource)
        manufacturerEditProfileBudgetEstTools=findViewById(R.id.etEditManufacurerBudgetEstTools)
        updateManufacturerProfileButton=findViewById(R.id.updateManufacturerProfileButton)
        val toolbarManufacturerEditProfile=findViewById<Toolbar>(R.id.toolbar_Manufacurer_edit_profile)
        setSupportActionBar(toolbarManufacturerEditProfile)
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
        manufacturerEditProfileImage.setOnClickListener {
            openGallery()
        }

        // Set click listener for update button
        updateManufacturerProfileButton.setOnClickListener {
            updateProfileData()
        }


    }


    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val manufacturerData = snapshot.getValue(Manufacturer::class.java)
            if (manufacturerData != null) {
                manufacturerEditProfileName.setText(manufacturerData.fullname)
                manufacturerEditProfileEmail.setText(manufacturerData.email)
                manufacturerEditProfileMobile.setText(manufacturerData.mobile)
                manufacturerEditProfileLinkedIn.setText(manufacturerData.linkedIn)
                manufacturerEditProfileCompanyName.setText(manufacturerData.companyName)
                manufacturerEditProfileCompanyAddress.setText(manufacturerData.companyAddress)
                manufacturerEditProfileExperience.setText(manufacturerData.experience)
                manufacturerEditProfileHighestQualification.setText(manufacturerData.highestQualification)
                manufacturerEditProfileLanguageKnown.setText(manufacturerData.languageKnown)
                manufacturerEditProfileSkills.setText(manufacturerData.skills)
                manufacturerEditProfileManPowerAvailable.setText(manufacturerData.manPower)
                manufacturerEditProfileMachineResources.setText(manufacturerData.machineResourceAvailable)
                manufacturerEditProfileBudgetEstTools.setText(manufacturerData.tools)

                // Load profile image
                val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
                val profileImageRef = storageRef.child(imagePath)

                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_internet_connection)
                        .into(manufacturerEditProfileImage)
                }.addOnFailureListener {
                    manufacturerEditProfileImage.setImageResource(R.drawable.sample_logo)
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
            "fullname" to manufacturerEditProfileName.text.toString(),
            "email" to manufacturerEditProfileEmail.text.toString(),
            "mobile" to manufacturerEditProfileMobile.text.toString(),
            "linkedIn" to manufacturerEditProfileLinkedIn.text.toString(),
            "companyName" to manufacturerEditProfileCompanyName.text.toString(),
            "companyAddress" to manufacturerEditProfileCompanyAddress.text.toString(),
            "experience" to manufacturerEditProfileExperience.text.toString(),
            "highestQualification" to manufacturerEditProfileHighestQualification.text.toString(),
            "languageKnown" to manufacturerEditProfileLanguageKnown.text.toString(),
            "skills" to manufacturerEditProfileSkills.text.toString(),
            "manPower" to manufacturerEditProfileManPowerAvailable.text.toString(),
            "machineResourceAvailable" to manufacturerEditProfileMachineResources.text.toString(),
            "tools" to manufacturerEditProfileBudgetEstTools.text.toString()
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



//        private fun updateProfileData() {
//        val userId = auth.currentUser?.uid ?: return
//        val updatedData = Manufacturer(
//            fullname = manufacturerEditProfileName.text.toString(),
//            email = manufacturerEditProfileEmail.text.toString(),
//            mobile = manufacturerEditProfileMobile.text.toString(),
//            linkedIn = manufacturerEditProfileLinkedIn.text.toString(),
//            companyName = manufacturerEditProfileCompanyName.text.toString(),
//            companyAddress = manufacturerEditProfileCompanyAddress.text.toString(),
//            experience = manufacturerEditProfileExperience.text.toString(),
//            highestQualification = manufacturerEditProfileHighestQualification.text.toString(),
//            languageKnown = manufacturerEditProfileLanguageKnown.text.toString(),
//            skills = manufacturerEditProfileSkills.text.toString(),
//            manPower= manufacturerEditProfileManPowerAvailable.text.toString(),
//            machineResourceAvailable = manufacturerEditProfileMachineResources.text.toString(),
//            tools = manufacturerEditProfileBudgetEstTools.text.toString()
//
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