package com.example.projectone.client

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ClientFeedbackForm : AppCompatActivity() {
    private lateinit var clientFeedbackToSalesPersonRatingBar:RatingBar
    private lateinit var clientFeedbackToSalesPersonClientId:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonSalesPersonId:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonEmail:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonOrderId:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonMobileNo:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonFeedbackBox:TextInputEditText
    private lateinit var clientFeedbackToSalesPersonAttachScreenShots:ImageButton
    private lateinit var btnSubmitClientFeedback:Button

    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth

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
        setContentView(R.layout.activity_client_feedback_form)

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance().reference
        storageRef = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        clientFeedbackToSalesPersonRatingBar=findViewById(R.id.clientRatingBar)
        clientFeedbackToSalesPersonClientId=findViewById(R.id.etClientFeedbackFormClientId)
        clientFeedbackToSalesPersonSalesPersonId=findViewById(R.id.etClientFeedbackFormSalesPersonId)
        clientFeedbackToSalesPersonEmail=findViewById(R.id.etClientFeedbackFormEmail)
        clientFeedbackToSalesPersonOrderId=findViewById(R.id.etClientFeedbackFormOrderId)
        clientFeedbackToSalesPersonMobileNo=findViewById(R.id.etClientFeedbackFormMobileNumber)
        clientFeedbackToSalesPersonFeedbackBox=findViewById(R.id.etClientFeedbackFormFeedbackbox)
        clientFeedbackToSalesPersonAttachScreenShots=findViewById(R.id.attachScreenShots)
        btnSubmitClientFeedback=findViewById(R.id.btnClientFeedback)
        val toolbarClientFeedbackForm=findViewById<Toolbar>(R.id.toolbarClientFeedbackForm)
        setSupportActionBar(toolbarClientFeedbackForm)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.subtitle="Feedback"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // Attach screenshot button click
        clientFeedbackToSalesPersonAttachScreenShots.setOnClickListener {
            openGallery()
        }

        // Fetch client details based on clientId
        clientFeedbackToSalesPersonClientId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                fetchClientDetails(clientFeedbackToSalesPersonClientId.text.toString())
            }
        }
        btnSubmitClientFeedback.setOnClickListener {
            submitFeedback()

        }


    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val clientId = clientFeedbackToSalesPersonClientId.text.toString()
        val imagePath = "client_feedback_screenshots/$clientId/${System.currentTimeMillis()}.jpg"
        val imageRef = storageRef.child(imagePath)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Screenshot uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload screenshot", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchClientDetails(clientId: String) {
        val clientRef = database.child("clients").child(clientId)
        clientRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val email = snapshot.child("email").getValue(String::class.java)
                val mobileNo = snapshot.child("mobile").getValue(String::class.java)
                clientFeedbackToSalesPersonEmail.setText(email)
                clientFeedbackToSalesPersonMobileNo.setText(mobileNo)
            } else {
                Toast.makeText(this, "Client not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch client details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitFeedback() {
        val ratings = clientFeedbackToSalesPersonRatingBar.rating.toString() // Convert rating to String
        val salesPersonId = clientFeedbackToSalesPersonSalesPersonId.text.toString().trim()
        val clientId = clientFeedbackToSalesPersonClientId.text.toString().trim()
        val email = clientFeedbackToSalesPersonEmail.text.toString().trim()
        val mobileNo = clientFeedbackToSalesPersonMobileNo.text.toString().trim()
        val feedbackbox = clientFeedbackToSalesPersonFeedbackBox.text.toString().trim()

        // Validate fields
        if (ratings.isEmpty() || ratings == "0.0") { // Check if rating is empty or zero
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
            return
        }
        if (salesPersonId.isEmpty()) {
            Toast.makeText(this, "Sales Person ID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (clientId.isEmpty()) {
            Toast.makeText(this, "Client ID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (mobileNo.isEmpty()) {
            Toast.makeText(this, "Mobile Number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (feedbackbox.isEmpty()) {
            Toast.makeText(this, "Feedbackbox box cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a feedback object
        val feedbackData = mapOf(
            "ratings" to ratings, // Store rating as String
            "clientId" to clientId,
            "email" to email,
            "mobile" to mobileNo,
            "feedbackbox" to feedbackbox
        )

        // Store the feedback data in Firebase
        val salesPersonRef = database.child("users").child(salesPersonId).child("feedback").push()
        salesPersonRef.setValue(feedbackData)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show()
                // Optionally, clear fields or navigate back
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        clientFeedbackToSalesPersonRatingBar.rating = 0f
        clientFeedbackToSalesPersonClientId.text?.clear()
        clientFeedbackToSalesPersonSalesPersonId.text?.clear()
        clientFeedbackToSalesPersonEmail.text?.clear()
        clientFeedbackToSalesPersonOrderId.text?.clear()
        clientFeedbackToSalesPersonMobileNo.text?.clear()
        clientFeedbackToSalesPersonFeedbackBox.text?.clear()
        clientFeedbackToSalesPersonAttachScreenShots.setImageResource(R.drawable.baseline_add_a_photo_24) // Assuming you have a default image resource
    }




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