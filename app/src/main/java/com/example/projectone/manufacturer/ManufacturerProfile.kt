package com.example.projectone.manufacturer

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
import com.example.projectone.manufacturer.manufacturerdatamodels.Manufacturer
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

class ManufacturerProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var toolbarManufacturerProfile:Toolbar
    private lateinit var manufacturerProfilBudgetEstimationTools:TextView
    private lateinit var manufacturerProfileMachineResourcesAvailable:TextView
    private lateinit var manufacturerProfileImage:ShapeableImageView
    private lateinit var manufacturerProfileStatus:TextView
    private lateinit var manufacturerProfileSkills:TextView
    private lateinit var manufacturerProfileEmail:TextView
    private lateinit var manufacturerProfileLanguageKnown:TextView
    private lateinit var manufacturerProfileName:TextView
    private lateinit var manufacturerProfileRatings:TextView
    private lateinit var manufacturerProfileRole:TextView
    private lateinit var manufacturerProfileMobileNumber:TextView
    private lateinit var manufacturerProfileEmpId:TextView
    private lateinit var manufacturerProfileCompanyName:TextView
    private lateinit var manufacturerProfileCompanyAddress:TextView
    private lateinit var manufacturerProfileExperience:TextView
    private lateinit var manufacturerProfileHighestQualification:TextView
    private lateinit var manufacturerProfileManpowerAvailable:TextView
    private lateinit var manufacturerProfileDoj:TextView
    private lateinit var manufacturerProfileLinkedIn:TextView
    private lateinit var manufacturerEditProfileFAB:FloatingActionButton
    private lateinit var manufacturerProfileBottom:BottomNavigationView

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
        setContentView(R.layout.activity_manufacturer_profile)
        toolbarManufacturerProfile=findViewById(R.id.toolbarManufacturerProfile)
        manufacturerProfilBudgetEstimationTools=findViewById(R.id.manufacturerProfilBudgetEstimationTools)
        manufacturerProfileMachineResourcesAvailable=findViewById(R.id.manufacturerProfileMachineResourcesAvailable)
        manufacturerProfileStatus=findViewById(R.id.manufacturerProfileStatus)
        manufacturerProfileSkills=findViewById(R.id.manufacturerProfileSkills)
        manufacturerProfileEmail=findViewById(R.id.manufacturerProfileEmail)
        manufacturerProfileImage=findViewById(R.id.manufacturerProfileImage)
        manufacturerProfileLanguageKnown=findViewById(R.id.manufacturerProfileLanguageKnown)
        manufacturerProfileName=findViewById(R.id.manufacturerProfileName)
        manufacturerProfileRatings=findViewById(R.id.manufacturerRatings)
        manufacturerProfileRole=findViewById(R.id.manufacturerProfileRole)
        manufacturerProfileMobileNumber=findViewById(R.id.manufacturerProfileMobileNumber)
        manufacturerProfileEmpId=findViewById(R.id.manufacturerProfileEmpId)
        manufacturerProfileCompanyName=findViewById(R.id.manufacturerProfileCompanyName)
        manufacturerProfileCompanyAddress=findViewById(R.id.manufacturerProfileCompanyAddress)
        manufacturerProfileExperience=findViewById(R.id.manufacturerProfileExperience)
        manufacturerProfileHighestQualification=findViewById(R.id.manufacturerProfileHighestQualification)
        manufacturerProfileManpowerAvailable=findViewById(R.id.manufacturerProfileManpowerAvailable)
        manufacturerProfileDoj=findViewById(R.id.manufacturerProfileDoj)
        manufacturerProfileLinkedIn=findViewById(R.id.manufacturerProfileLinkedIn)
        manufacturerEditProfileFAB=findViewById(R.id.manufacturerEditProfileFAB)


        //init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        manufacturerEditProfileFAB.setOnClickListener {
            startActivity(Intent(this,ManufacturerEditProfile::class.java))
        }

        loadManufacturersProfileData()

        marqueeEffect()
        setSupportActionBar(toolbarManufacturerProfile)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Profile"

        manufacturerProfileImage.setOnLongClickListener {
            openGallery()
            true
        }


        // Image preview on click
        manufacturerProfileImage.setOnClickListener {
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



        manufacturerProfileBottom=findViewById(R.id.manufacturerProfileBottom)
        manufacturerProfileBottom.selectedItemId=R.id.manufacturerProfileMenu
        manufacturerProfileBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
    }

    private fun marqueeEffect() {
        manufacturerProfilBudgetEstimationTools.isSelected=true
        manufacturerProfileLanguageKnown.isSelected=true
        manufacturerProfileEmail.isSelected=true
        manufacturerProfileSkills.isSelected=true
        manufacturerProfileStatus.isSelected=true
        manufacturerProfileName.isSelected=true
        manufacturerProfileMachineResourcesAvailable.isSelected=true
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

                // Optionally, you can store the image URL in the Realtime Database
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userRef = database.child("users").child(userId)
                    userRef.child("profileImageUrl").setValue(uri.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }


                // Update UI to reflect the new photo
                loadManufacturersProfileData()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadManufacturersProfileData() {
        val userId=auth.currentUser?.uid?:return
        val userRef= database.child("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val manufacturerData=snapshot.getValue(Manufacturer::class.java)
                    if (manufacturerData!=null){
                        populateManufacturerData(manufacturerData)
                    }else{
                        Toast.makeText(this@ManufacturerProfile, "Manufacturer data not found", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this@ManufacturerProfile, "No data available", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManufacturerProfile, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun populateManufacturerData(manufacturerData: Manufacturer) {
        manufacturerProfileName.text=manufacturerData.fullname
        manufacturerProfileEmail.text=manufacturerData.email
        manufacturerProfileMobileNumber.text=manufacturerData.mobile
        manufacturerProfileEmpId.text=manufacturerData.empId
        manufacturerProfileRole.text=manufacturerData.role
        manufacturerProfileLinkedIn.text=manufacturerData.linkedIn
        manufacturerProfileCompanyName.text=manufacturerData.companyName
        manufacturerProfileCompanyAddress.text=manufacturerData.companyAddress
        manufacturerProfileDoj.text=manufacturerData.doj
        manufacturerProfileHighestQualification.text=manufacturerData.highestQualification
        manufacturerProfileSkills.text=manufacturerData.skills
        manufacturerProfileExperience.text=manufacturerData.experience
        manufacturerProfileLanguageKnown.text=manufacturerData.languageKnown
        manufacturerProfilBudgetEstimationTools.text=manufacturerData.tools
        manufacturerProfileRatings.text=manufacturerData.ratings


        // Load profile image from Firebase Storage
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            if (!isDestroyed) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.no_internet_connection) // Error image if loading fails
                    .into(manufacturerProfileImage)
            }
        }.addOnFailureListener {
            if (!isDestroyed) {
                // Handle any errors
                manufacturerProfileImage.setImageResource(R.drawable.sample_logo) // Default image
            }
        }


    }


    private fun handleNavigation(itemId: Int):Boolean {
        if (itemId==manufacturerProfileBottom.selectedItemId){
            return true
        }

        when(itemId){
            R.id.manufacturerProfileMenu->{
                return true
            }
            R.id.manufacturerProjectsMenu->{
                startActivity(Intent(this@ManufacturerProfile,ManufacturerProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.manufacturerSettingsMenu->{
                startActivity(Intent(this@ManufacturerProfile,ManufacturerSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
        }

    }
}