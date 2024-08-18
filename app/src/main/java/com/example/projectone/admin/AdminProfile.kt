package com.example.projectone.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.ImagePreviewActivity
import com.example.projectone.admin.admindatamodels.Admin
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class AdminProfile : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var adminProfileImage: CircleImageView
    private lateinit var adminProfileCompanyNameAbove: TextView
    private lateinit var adminProfileCompanyRoleAbove: TextView
    private lateinit var adminProfileFullName: TextView
    private lateinit var adminProfileGender: TextView
    private lateinit var adminProfileEmail: TextView
    private lateinit var adminProfileMobileNumber: TextView
    private lateinit var adminProfileEmpId: TextView
    private lateinit var adminProfilePosition: TextView
    private lateinit var adminProfileDateOfJoining: TextView
    private lateinit var adminProfileCompanyName: TextView
    private lateinit var adminProfileCompanyEmail: TextView
    private lateinit var adminProfileCompanyAddress: TextView
    private lateinit var adminProfileRoleOfAccess: TextView
    private lateinit var adminProfileDepartment: TextView
    private lateinit var adminProfileLinkedInTv: TextView

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
        setContentView(R.layout.activity_admin_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Initialize views
        val toolbaradminProfile = findViewById<Toolbar>(R.id.toolbaradminProfile)
        adminProfileImage = findViewById(R.id.adminProfileImage)
        adminProfileCompanyRoleAbove = findViewById(R.id.adminProfileCompanyRoleAbove)
        adminProfileCompanyNameAbove = findViewById(R.id.adminProfileCompanyNameAbove)
        adminProfileFullName = findViewById(R.id.adminProfileFullName)
        adminProfileGender = findViewById(R.id.adminProfileGender)
        adminProfileEmail = findViewById(R.id.adminProfileEmail)
        adminProfileMobileNumber = findViewById(R.id.adminProfileMobileNo)
        adminProfileEmpId = findViewById(R.id.adminProfileEmpId)
        adminProfilePosition = findViewById(R.id.adminProfilePosition)
        adminProfileDateOfJoining = findViewById(R.id.adminProfileDateOfJoining)
        adminProfileCompanyName = findViewById(R.id.adminProfileCompanyName)
        adminProfileCompanyEmail = findViewById(R.id.adminProfileCompanyEmail)
        adminProfileCompanyAddress = findViewById(R.id.adminProfileCompanyAddress)
        adminProfileRoleOfAccess = findViewById(R.id.adminProfileRoleOfAccess)
        adminProfileDepartment = findViewById(R.id.adminProfileDepartment)
        adminProfileLinkedInTv = findViewById(R.id.adminProfileLinkedInTv)

        // Marquee effect
        setMarqueeEffect()

        setSupportActionBar(toolbaradminProfile)
        supportActionBar?.title = ""
        supportActionBar?.subtitle = "Profile"

        bottomNav = findViewById(R.id.adminBottomNav)
        bottomNav.selectedItemId = R.id.adminProfileMenu
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
        }

        // Load admin profile data
        loadAdminProfileData()

        // Set up click listener for profile image
        adminProfileImage.setOnLongClickListener {
            openGallery()
            true
        }

        // Image preview on click
        adminProfileImage.setOnClickListener {
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

    private fun setMarqueeEffect() {
        adminProfileFullName.isSelected = true
        adminProfileEmail.isSelected = true
        adminProfileCompanyName.isSelected = true
        adminProfileCompanyEmail.isSelected = true
        adminProfileCompanyAddress.isSelected = true
        adminProfileRoleOfAccess.isSelected = true
        adminProfileDepartment.isSelected = true
        adminProfileLinkedInTv.isSelected = true
    }

    private fun loadAdminProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val adminData = dataSnapshot.getValue(Admin::class.java)
                    if (adminData != null) {
                        populateProfileData(adminData)
                    } else {
                        Toast.makeText(this@AdminProfile, "Admin data not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AdminProfile, "No data available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AdminProfile, "Failed to load data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateProfileData(admin: Admin) {
        adminProfileFullName.text = admin.fullname
        adminProfileGender.text = admin.gender
        adminProfileEmail.text = admin.email
        adminProfileMobileNumber.text = admin.mobile
        adminProfileEmpId.text = admin.empId
        adminProfilePosition.text = admin.role // This is the role
        adminProfileDateOfJoining.text = admin.doj
        adminProfileCompanyName.text = admin.companyName
        adminProfileCompanyEmail.text = admin.companyEmail
        adminProfileCompanyAddress.text = admin.companyAddress
        adminProfileRoleOfAccess.text = admin.roleOfAccess
        adminProfileDepartment.text = admin.department
        adminProfileLinkedInTv.text = admin.linkedIn
        adminProfileCompanyNameAbove.text = admin.companyName
        adminProfileCompanyRoleAbove.text = admin.role

        // Load profile image from Firebase Storage
        val imagePath = "profile_images/${auth.currentUser?.uid}.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            if (!isDestroyed) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.no_internet_connection) // Error image if loading fails
                    .into(adminProfileImage)
            }
        }.addOnFailureListener {
            if (!isDestroyed) {
                // Handle any errors
                adminProfileImage.setImageResource(R.drawable.sample_logo) // Default image
            }
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
                loadAdminProfileData()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.adminprofileedit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.adminprofileeditmenu -> {
                startActivity(Intent(this, AdminEditProfile::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == bottomNav.selectedItemId) {
            return true
        }

        return when (itemId) {
            R.id.adminOngoingProjectsMenu -> {
                startActivity(Intent(this, AdminOngoingProjects::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.adminaddAddEmployeesMenu -> {
                startActivity(Intent(this, AdminAddEmployees::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.adminManufactringInfoMenu -> {
                startActivity(Intent(this, AdminManufacturerInfo::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.adminProfileMenu -> true
            R.id.adminMoreMenu -> {
                startActivity(Intent(this, AdminMoreSettings::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            else -> false
        }
    }
}

// Data class for Admin profile
//data class Admin(
//    val fullname: String = "",
//    val email: String = "",
//    val mobile: String = "",
//    val empId: String = "",
//    val role: String = "", // This represents the role
//    val doj: String = "",
//    val companyName: String = "",
//    val companyEmail: String = "",
//    val companyAddress: String = "",
//    val roleOfAccess: String = "",
//    val department: String = "",
//    val linkedIn: String = "",
//    val experience: String = "",
//    val highestQualification: String = "",
//    val languagesKnown: String = "",
//    val skills: String = "",
//    val gender: String = "" // Optional field
//)
