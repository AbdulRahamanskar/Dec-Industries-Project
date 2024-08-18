package com.example.projectone.salesperson

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.*

class RedirectUserprofile : AppCompatActivity() {
    private lateinit var redirectUserProfileImage: ShapeableImageView
    private lateinit var redirectUserProfileStatus: TextView
    private lateinit var redirectUserProfileRatings: TextView
    private lateinit var redirectUserProfileTargets: TextView
    private lateinit var redirectUserProfileFullName: TextView
    private lateinit var redirectUserProfileRole: TextView
    private lateinit var redirectUserProfileEmail: TextView
    private lateinit var redirectUserProfileMobile: TextView
    private lateinit var redirectUserProfileEmpId: TextView
    private lateinit var redirectUserProfileCompanyName: TextView
    private lateinit var redirectUserProfileCompanyAddress: TextView
    private lateinit var redirectUserProfileExperience: TextView
    private lateinit var redirectUserProfileLinkedIn: TextView
    private lateinit var redirectUserProfileDoj: TextView
    private lateinit var redirectUserProfileQualification: TextView
    private lateinit var redirectUserProfileSkills: TextView
    private lateinit var redirectUserProfileLanguagesKnown: TextView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redirect_userprofile)

        // Initialize UI elements
        redirectUserProfileImage = findViewById(R.id.redirectUserProfileImage)
        redirectUserProfileStatus = findViewById(R.id.redirectUserProfileStatus)
        redirectUserProfileRatings = findViewById(R.id.redirectUserProfileRatings)
        redirectUserProfileTargets = findViewById(R.id.redirectUserProfileTargets)
        redirectUserProfileFullName = findViewById(R.id.redirectUserProfileFullName)
        redirectUserProfileRole = findViewById(R.id.redirectUserProfileRole)
        redirectUserProfileEmail = findViewById(R.id.redirectUserProfileEmail)
        redirectUserProfileMobile = findViewById(R.id.redirectUserProfileMobile)
        redirectUserProfileEmpId = findViewById(R.id.redirectUserProfileEmpId)
        redirectUserProfileCompanyName = findViewById(R.id.redirectUserProfileCompanyName)
        redirectUserProfileCompanyAddress = findViewById(R.id.redirectUserProfileCompanyAddress)
        redirectUserProfileExperience = findViewById(R.id.redirectUserProfileExperience)
        redirectUserProfileLinkedIn = findViewById(R.id.redirectUserProfileLinkedIn)
        redirectUserProfileDoj = findViewById(R.id.redirectUserProfileDoj)
        redirectUserProfileQualification = findViewById(R.id.redirectUserProfileQualification)
        redirectUserProfileSkills = findViewById(R.id.redirectUserProfileSkills)
        redirectUserProfileLanguagesKnown = findViewById(R.id.redirectUserProfileLanguagesKnown)

        // Get the user profile ID from the intent
        val userProfileId = intent.getStringExtra("USER_PROFILE_ID")
        Log.d("RedirectUserprofile", "UserProfileId: $userProfileId")

        if (userProfileId != null && userProfileId.isNotEmpty()) {
            // Initialize Firebase reference
            databaseReference = FirebaseDatabase.getInstance().getReference("salespersons").child(userProfileId)
            Log.d("RedirectUserprofile", "Database Reference Path: ${databaseReference.path}")
            // Fetch user data from Firebase
            fetchUserProfileData()
        } else {

            Toast.makeText(this, "User profile ID not found", Toast.LENGTH_SHORT).show()
        }
    }




    private fun fetchUserProfileData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RedirectUserprofile", "Data snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    val salesPerson = snapshot.getValue(SalesPerson::class.java)
                    Log.d("RedirectUserprofile", "SalesPerson data: $salesPerson")

                    if (salesPerson != null) {
                        // Use default values if any fields are null
                        Glide.with(this@RedirectUserprofile)
                            .load(salesPerson.profileImageUrl ?: R.drawable.sample_logo) // Default image if URL is null
                            .placeholder(R.drawable.sample_logo)
                            .into(redirectUserProfileImage)

                        redirectUserProfileStatus.text = salesPerson.onlineStatus ?: "N/A"
                        redirectUserProfileRatings.text = salesPerson.ratings.toString() ?: "N/A"
                        redirectUserProfileTargets.text = salesPerson.salesTargetsCompleted ?: "N/A"
                        redirectUserProfileFullName.text = salesPerson.fullname ?: "N/A"
                        redirectUserProfileRole.text = salesPerson.role ?: "N/A"
                        redirectUserProfileEmail.text = salesPerson.email ?: "N/A"
                        redirectUserProfileMobile.text = salesPerson.mobile ?: "N/A"
                        redirectUserProfileEmpId.text = salesPerson.empId ?: "N/A"
                        redirectUserProfileCompanyName.text = salesPerson.companyName ?: "N/A"
                        redirectUserProfileCompanyAddress.text = salesPerson.companyAddress ?: "N/A"
                        redirectUserProfileExperience.text = salesPerson.experience ?: "N/A"
                        redirectUserProfileLinkedIn.text = salesPerson.linkedIn ?: "N/A"
                        redirectUserProfileDoj.text = salesPerson.doj ?: "N/A"
                        redirectUserProfileQualification.text = salesPerson.highestQualification ?: "N/A"
                        redirectUserProfileSkills.text = salesPerson.skills ?: "N/A"
                        redirectUserProfileLanguagesKnown.text = salesPerson.languageKnown ?: "N/A"
                    } else {
                        Toast.makeText(this@RedirectUserprofile, "SalesPerson data not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RedirectUserprofile, "No data found for userProfileId", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RedirectUserprofile", "DatabaseError: ${error.message}")
                Toast.makeText(this@RedirectUserprofile, "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
