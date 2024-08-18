package com.example.projectone.client

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
import com.bumptech.glide.Glide
import com.example.projectone.ImagePreviewActivity
import com.example.projectone.R
import com.example.projectone.client.clientdatamodels.Client
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

class ClientProfile : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var storage:FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var clientProfileImage:ShapeableImageView
    private lateinit var clientProfileSince:TextView
    private lateinit var clientProfileDoj:TextView
    private lateinit var clientProfileCompletedProjects:TextView
    private lateinit var clientProfileName:TextView
    private lateinit var clientProfileRole:TextView
    private lateinit var clientProfileEmail:TextView
    private lateinit var clientProfileMobileNumber:TextView
    private lateinit var clientProfileClientId:TextView
    private lateinit var clientProfileCompanyName:TextView
    private lateinit var clientProfileCompanyAddress:TextView
    private lateinit var clientProfileExperience:TextView
    private lateinit var clientProfileHighestQualification:TextView
    private lateinit var clientProfileSkills:TextView
    private lateinit var clientProfileLanguageKnown:TextView
    private lateinit var clientProfileStatus:TextView

    private lateinit var clientProfileBottom:BottomNavigationView

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
        setContentView(R.layout.activity_client_profile)
        //init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference



            //views
        clientProfileImage=findViewById(R.id.clientProfileImage)
        clientProfileSince=findViewById(R.id.clientProfileSince)
        clientProfileDoj=findViewById(R.id.clientProfileDoj)
        clientProfileCompletedProjects=findViewById(R.id.clientProfileCompletedProjects)
        clientProfileName=findViewById(R.id.clientProfileName)
        clientProfileRole=findViewById(R.id.clientProfileRole)
        clientProfileEmail=findViewById(R.id.clientProfileEmail)
        clientProfileMobileNumber=findViewById(R.id.clientProfileMobileNumber)
        clientProfileClientId=findViewById(R.id.clientProfileClientId)
        clientProfileCompanyName=findViewById(R.id.clientProfileCompanyName)
        clientProfileCompanyAddress=findViewById(R.id.clientProfileCompanyAddress)
        clientProfileExperience=findViewById(R.id.clientProfileExperience)
        clientProfileHighestQualification=findViewById(R.id.clientProfileHighestQualification)
        clientProfileSkills=findViewById(R.id.clientProfileSkills)
        clientProfileLanguageKnown=findViewById(R.id.clientProfileLanguageKnown)

        clientProfileBottom=findViewById(R.id.clientBottom)
        val clientEditProfileFAB=findViewById<FloatingActionButton>(R.id.clientEditProfileFAB)
        clientProfileStatus=findViewById<TextView>(R.id.clientProfileStatus)
        loadClientProfileData()

        setMarqueeEffect()


        // Set up click listener for profile image
        clientProfileImage.setOnLongClickListener {
            openGallery()
            true
        }

        // Image preview on click
        clientProfileImage.setOnClickListener {
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



        clientEditProfileFAB.setOnClickListener {
            startActivity(Intent(this,ClientEditProfile::class.java))
        }

        clientProfileBottom.selectedItemId=R.id.clientProfileMenu

        clientProfileBottom.setOnNavigationItemSelectedListener {
            navigationHandler(it.itemId)
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
                loadClientProfileData()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }



    private fun setMarqueeEffect() {
        //marquee
        clientProfileSkills.isSelected=true
        clientProfileStatus.isSelected=true
        clientProfileHighestQualification.isSelected=true
        clientProfileCompanyName.isSelected=true
        clientProfileLanguageKnown.isSelected=true
        clientProfileCompanyAddress.isSelected=true
        clientProfileName.isSelected=true
        clientProfileMobileNumber.isSelected=true
        clientProfileEmail.isSelected=true
    }

    private fun loadClientProfileData() {
        val userId=auth.currentUser?.uid?:return
        val userRef= database.child("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val clientData=snapshot.getValue(Client::class.java)
                    if (clientData!=null){
                        populateClientData(clientData)
                    }else{
                        Toast.makeText(this@ClientProfile, "Client data not found", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(this@ClientProfile, "No data available", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientProfile, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun populateClientData(clientData: Client) {
        clientProfileName.text=clientData.fullname
        clientProfileEmail.text=clientData.email
        clientProfileMobileNumber.text=clientData.mobile
        clientProfileClientId.text=clientData.clientId
        clientProfileRole.text=clientData.role
        clientProfileDoj.text=clientData.doj
        clientProfileCompanyName.text=clientData.companyName
        clientProfileSince.text=clientData.since
        clientProfileCompletedProjects.text=clientData.completedProjects
        clientProfileHighestQualification.text=clientData.highestQualification
        clientProfileSkills.text=clientData.skills
        clientProfileExperience.text=clientData.experience
        clientProfileLanguageKnown.text=clientData.languageKnown


        // Load profile image from Firebase Storage
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            if (!isDestroyed) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.no_internet_connection) // Error image if loading fails
                    .into(clientProfileImage)
            }
        }.addOnFailureListener {
            if (!isDestroyed) {
                // Handle any errors
                clientProfileImage.setImageResource(R.drawable.sample_logo) // Default image
            }
        }


    }

    private fun navigationHandler(itemId: Int):Boolean {
        if (itemId==clientProfileBottom.selectedItemId){
            return true
        }
        when(itemId){
            R.id.clientProfileMenu->{
                finish()
                return true
            }
            R.id.clientProjectsMenu->{
                startActivity(Intent(this@ClientProfile,ClientProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.clientSettingsMenu->{
                startActivity(Intent(this@ClientProfile,ClientSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
        }
    }
}
//data class Client(
//    val fullname:String="",
//    val email:String="",
//    val mobile:String="",
//    val clientId:String="",
//    val role:String="",
//    val doj:String="",
//    val companyName:String="",
//    val since:String="",
//    val completedProjects:String="",
//    val highestQualification:String="",
//    val skills:String="",
//    val experience:String="",
//    val languageKnown:String="",
//    val linkedIn:String="",
//    val companyAddress:String=""
//
//)



