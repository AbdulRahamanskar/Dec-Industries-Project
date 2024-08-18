package com.example.projectone.client

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.client.clientdatamodels.Client
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ClientEditProfile : AppCompatActivity() {

    private lateinit var clientEditProfileImage: ShapeableImageView
    private lateinit var clientEditProfileFullName: TextInputEditText
    private lateinit var clientEditProfileEmail: TextInputEditText
    private lateinit var clientEditProfileMobileNumber: TextInputEditText
    private lateinit var clientEditProfileLinkedIn: TextInputEditText
    private lateinit var clientEditProfileCompanyName: TextInputEditText
    private lateinit var clientEditProfileCompanyRegion: TextInputEditText
    private lateinit var clientEditProfileExperience: TextInputEditText
    private lateinit var clientEditProfileHighestQualification: TextInputEditText
    private lateinit var clientEditProfileLanguaguesKnown: TextInputEditText
    private lateinit var clientEditProfileSkills: TextInputEditText
    private lateinit var btnClientProfileditUpdate: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Initialize views
        clientEditProfileImage = findViewById(R.id.clientEditProfileImage)
        clientEditProfileFullName = findViewById(R.id.etEditClientFullName)
        clientEditProfileEmail = findViewById(R.id.etEditClientEmail)
        clientEditProfileMobileNumber = findViewById(R.id.etEditClientMobileNumber)
        clientEditProfileLinkedIn = findViewById(R.id.etEditClientLinkedIn)
        clientEditProfileCompanyName = findViewById(R.id.etEditClientCompanyName)
        clientEditProfileCompanyRegion = findViewById(R.id.etEditClientCompanyRegion)
        clientEditProfileExperience = findViewById(R.id.etEditClientExperience)
        clientEditProfileHighestQualification = findViewById(R.id.etEditClientHighestQualification)
        clientEditProfileLanguaguesKnown = findViewById(R.id.etEditClientLanguaguesKnown)
        clientEditProfileSkills = findViewById(R.id.etEditClientSkills)
        btnClientProfileditUpdate = findViewById(R.id.btnClientProfileditUpdate)

        val toolbarClientEditProfile = findViewById<Toolbar>(R.id.toolbar_client_edit_profile)
        setSupportActionBar(toolbarClientEditProfile)
        supportActionBar?.apply {
            title = ""
            subtitle = "Edit Profile"
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            setDisplayHomeAsUpEnabled(true)
        }

        // Load existing client data
        loadClientData()

        // Update client profile data
        btnClientProfileditUpdate.setOnClickListener {
            updateClientProfile()
        }
    }

    private fun loadClientData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val clientData = snapshot.getValue(Client::class.java)
                if (clientData != null) {
                    populateFields(clientData)
                }
            } else {
                Toast.makeText(this, "Client data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateFields(clientData: Client) {
        clientEditProfileFullName.setText(clientData.fullname)
        clientEditProfileEmail.setText(clientData.email)
        clientEditProfileMobileNumber.setText(clientData.mobile)
        clientEditProfileLinkedIn.setText(clientData.linkedIn)
        clientEditProfileCompanyName.setText(clientData.companyName)
        clientEditProfileCompanyRegion.setText(clientData.companyAddress)
        clientEditProfileExperience.setText(clientData.experience)
        clientEditProfileHighestQualification.setText(clientData.highestQualification)
        clientEditProfileLanguaguesKnown.setText(clientData.languageKnown)
        clientEditProfileSkills.setText(clientData.skills)

        // Load profile image if needed
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.loading) // Placeholder image while loading
                .error(R.drawable.no_internet_connection) // Error image if loading fails
                .into(clientEditProfileImage)
        }.addOnFailureListener {
            clientEditProfileImage.setImageResource(R.drawable.sample_logo) // Default image
        }
    }
    private fun updateClientProfile() {
        val userId = auth.currentUser?.uid ?: return

        val clientData = mapOf(
            "fullname" to clientEditProfileFullName.text.toString(),
            "email" to clientEditProfileEmail.text.toString(),
            "mobile" to clientEditProfileMobileNumber.text.toString(),
            "linkedIn" to clientEditProfileLinkedIn.text.toString(),
            "companyName" to clientEditProfileCompanyName.text.toString(),
            "companyAddress" to clientEditProfileCompanyRegion.text.toString(),
            "experience" to clientEditProfileExperience.text.toString(),
            "highestQualification" to clientEditProfileHighestQualification.text.toString(),
            "languageKnown" to clientEditProfileLanguaguesKnown.text.toString(),
            "skills" to clientEditProfileSkills.text.toString()
        )

        database.child("users").child(userId).updateChildren(clientData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("UpdateProfile", "Failed to update profile", e)
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }



//    private fun updateClientProfile() {
//        val userId = auth.currentUser?.uid ?: return
//
//
//        val clientData = Client(
//            fullname = clientEditProfileFullName.text.toString(),
//            email = clientEditProfileEmail.text.toString(),
//            mobile = clientEditProfileMobileNumber.text.toString(),
//            linkedIn = clientEditProfileLinkedIn.text.toString(),
//            companyName = clientEditProfileCompanyName.text.toString(),
//            companyAddress = clientEditProfileCompanyRegion.text.toString(),
//            experience = clientEditProfileExperience.text.toString(),
//            highestQualification = clientEditProfileHighestQualification.text.toString(),
//            languageKnown = clientEditProfileLanguaguesKnown.text.toString(),
//            skills = clientEditProfileSkills.text.toString()
//        )
//
//        val userRef = database.child("users").child(userId)
//        userRef.setValue(clientData).addOnSuccessListener {
//            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//            finish() // Close the activity and return to the previous screen
//        }.addOnFailureListener { exception ->
//            Log.d("ClientEditProfile", "Failed to update profile: ${exception.message} ")
//            Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
//            // Add additional logging or debugging here if needed
//        }
//
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
