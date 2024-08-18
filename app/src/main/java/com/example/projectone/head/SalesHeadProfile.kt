package com.example.projectone.head

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.ImagePreviewActivity
import com.example.projectone.R
import com.example.projectone.head.salesheaddatamodels.SalesHead
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

class SalesHeadProfile : AppCompatActivity() {
    private lateinit var salesHeadProfileImage:ShapeableImageView
    private lateinit var salesHeadProfileOnlineStatus: TextView
    private lateinit var salesHeadProfileRating: TextView
    private lateinit var salesHeadProfileTotalRevnueManaged: TextView
    private lateinit var salesHeadProfileFullName: TextView
    private lateinit var salesHeadProfileRole: TextView
    private lateinit var salesHeadProfileEmail: TextView
    private lateinit var salesHeadProfileMobileNo: TextView
    private lateinit var salesHeadProfileEmpId: TextView
    private lateinit var salesHeadProfileCompanyName: TextView
    private lateinit var salesHeadProfileCompanyAddress: TextView
    private lateinit var salesHeadProfileExperience: TextView
    private lateinit var salesHeadProfileLinkedIn: TextView
    private lateinit var salesHeadProfileDoj: TextView
    private lateinit var salesHeadProfileHighestQualification: TextView
    private lateinit var salesHeadProfileSkills: TextView
    private lateinit var salesHeadProfileKeyClientHandled: TextView
    private lateinit var salesHeadProfileRecentProjectHandled: TextView
    private lateinit var salesHeadProfileLanguageKnown: TextView
    private lateinit var salesHeadProfileNoOfTeamsManged: TextView
    private lateinit var salesHeadEditProfileFAB: FloatingActionButton
    private lateinit var salesHeadProfileBottom:BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri)
                }
            }
        }


            @SuppressLint("SuspiciousIndentation")
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_profile)
        //views
        salesHeadEditProfileFAB=findViewById(R.id.salesHeadEditProfileFAB)
        salesHeadProfileImage=findViewById(R.id.salesHeadProfileImage)
        salesHeadProfileOnlineStatus=findViewById(R.id.salesHeadProfileStatus)
        salesHeadProfileRating=findViewById(R.id.salesHeadProfileRatings)
        salesHeadProfileTotalRevnueManaged=findViewById(R.id.salesHeadProfileSalesTotalrevunueManaged)
        salesHeadProfileFullName=findViewById(R.id.salesHeadProfileName)
        salesHeadProfileRole=findViewById(R.id.salesHeadProfileRole)
        salesHeadProfileEmail=findViewById(R.id.salesHeadProfileEmail)
        salesHeadProfileMobileNo=findViewById(R.id.salesHeadProfileMobileNumber)
        salesHeadProfileEmpId=findViewById(R.id.salesHeadProfileEmpId)
        salesHeadProfileCompanyName=findViewById(R.id.salesHeadProfileCompany)
        salesHeadProfileCompanyAddress=findViewById(R.id.salesHeadProfileCompanyRegion)
        salesHeadProfileExperience=findViewById(R.id.salesHeadProfileExperience)
        salesHeadProfileLinkedIn=findViewById(R.id.salesHeadProfileLinkedIn)
        salesHeadProfileDoj=findViewById(R.id.salesHeadProfileDoj)
        salesHeadProfileHighestQualification=findViewById(R.id.salesHeadProfileHighestQualification)
        salesHeadProfileSkills=findViewById(R.id.salesHeadProfileSkills)
        salesHeadProfileKeyClientHandled=findViewById(R.id.salesHeadProfileKeyClientsHandled)
        salesHeadProfileRecentProjectHandled=findViewById(R.id.salesHeadRecentProjectHandled)
        salesHeadProfileLanguageKnown=findViewById(R.id.salesHeadProfileLanguageKnown)
        salesHeadProfileNoOfTeamsManged=findViewById(R.id.salesHeadProfileNoOfteamsManage)
                //init
                auth = FirebaseAuth.getInstance()
                database = FirebaseDatabase.getInstance().reference
                storage = FirebaseStorage.getInstance()
                storageRef = storage.reference


                val toolbarSalesHeadProfile=findViewById<Toolbar>(R.id.toolbarSalesHeadProfile)
        salesHeadProfileBottom=findViewById(R.id.salesHeadProfileBottom)
        salesHeadProfileBottom.selectedItemId=R.id.salesHeadProfileMenu
        setSupportActionBar(toolbarSalesHeadProfile)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Profile"

                salesHeadEditProfileFAB.setOnClickListener {
                startActivity(Intent(this,SalesHeadEditProfile::class.java))
                }

        salesHeadProfileBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }


                //load data
                loadsalesHeadProfileData()
                //marquee effect for textview
                marqueeEffect()

                salesHeadProfileImage.setOnLongClickListener {
                    openGallery()
                    true
                }

                // Image preview on click
                salesHeadProfileImage.setOnClickListener {
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


    }



    private fun marqueeEffect() {
        salesHeadProfileFullName.isSelected=true
        salesHeadProfileEmail.isSelected=true
        salesHeadProfileCompanyName.isSelected=true
        salesHeadProfileCompanyAddress.isSelected=true
        salesHeadProfileHighestQualification.isSelected=true
        salesHeadProfileSkills.isSelected=true
        salesHeadProfileExperience.isSelected=true
        salesHeadProfileLanguageKnown.isSelected=true
        salesHeadProfileLinkedIn.isSelected=true
        salesHeadProfileTotalRevnueManaged.isSelected=true
        salesHeadProfileRating.isSelected=true
        salesHeadProfileOnlineStatus.isSelected=true
        salesHeadProfileRecentProjectHandled.isSelected=true
        salesHeadProfileKeyClientHandled.isSelected=true
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val imagePath = "profile_images/$userId.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    // Store the download URL in the Realtime Database
                    saveProfileImageUrlToDatabase(profileImageUrl)
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileImageUrlToDatabase(profileImageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        val updates = mapOf(
            "profileImageUrl" to profileImageUrl
        )

        userRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
            // Update UI to reflect the new photo
            loadsalesHeadProfileData()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update profile photo URL", Toast.LENGTH_SHORT).show()
        }
    }




//    private fun uploadImageToFirebase(imageUri: Uri) {
//        val userId = auth.currentUser?.uid ?: return
//        val imagePath = "profile_images/$userId.jpg"
//        val profileImageRef = storageRef.child(imagePath)
//
//        profileImageRef.putFile(imageUri)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
//                // Update UI to reflect the new photo
//                loadsalesHeadProfileData()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun loadsalesHeadProfileData() {
        val userId=auth.currentUser?.uid?:return
        val userRef= database.child("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val salesHeadData=snapshot.getValue(SalesHead::class.java)
                    if (salesHeadData!=null){
                        populateSalesHeadData(salesHeadData)
                    }else{
                        Toast.makeText(this@SalesHeadProfile, "Sales Head data not found", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this@SalesHeadProfile, "No data available", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesHeadProfile, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun populateSalesHeadData(salesHeadData: SalesHead) {
        salesHeadProfileFullName.text=salesHeadData.fullname
        salesHeadProfileEmail.text=salesHeadData.email
        salesHeadProfileMobileNo.text=salesHeadData.mobile
        salesHeadProfileEmpId.text=salesHeadData.empId
        salesHeadProfileRole.text=salesHeadData.role
        salesHeadProfileLinkedIn.text=salesHeadData.linkedIn
        salesHeadProfileCompanyName.text=salesHeadData.companyName
        salesHeadProfileCompanyAddress.text=salesHeadData.companyAddress
        salesHeadProfileDoj.text=salesHeadData.doj
        salesHeadProfileHighestQualification.text=salesHeadData.highestQualification
        salesHeadProfileSkills.text=salesHeadData.skills
        salesHeadProfileExperience.text=salesHeadData.experience
        salesHeadProfileLanguageKnown.text=salesHeadData.languageKnown
        salesHeadProfileTotalRevnueManaged.text=salesHeadData.totalRevenueManaged
        salesHeadProfileRating.text=salesHeadData.ratings
        salesHeadProfileNoOfTeamsManged.text=salesHeadData.noOfTeamsManaged


        // Load profile image from Firebase Storage
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            if (!isDestroyed) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.no_internet_connection) // Error image if loading fails
                    .into(salesHeadProfileImage)
            }
        }.addOnFailureListener {
            if (!isDestroyed) {
                // Handle any errors
                salesHeadProfileImage.setImageResource(R.drawable.sample_logo) // Default image
            }
        }


    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun handleNavigation(itemId: Int):Boolean {
        if (itemId==salesHeadProfileBottom.selectedItemId){
            return true
        }
        when(itemId){
            R.id.salesHeadProfileMenu->{
                return true
            }
            R.id.salesHeadProjectsMenu->{
                startActivity(Intent(this@SalesHeadProfile,SalesHeadProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesHeadSettingsMenu->{
                startActivity(Intent(this@SalesHeadProfile,SalesHeadSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else-> return false
        }
    }
}