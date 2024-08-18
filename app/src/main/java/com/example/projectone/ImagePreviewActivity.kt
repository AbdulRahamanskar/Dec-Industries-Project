package com.example.projectone

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var imageView: ShapeableImageView
    private lateinit var removeImageButton: ImageButton
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        val toolbarImagePreview=findViewById<Toolbar>(R.id.imagePreviewToolbar)
        setSupportActionBar(toolbarImagePreview)
        supportActionBar?.title=""
        supportActionBar?.subtitle=""
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_black_24)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        imageView = findViewById(R.id.imagePreview)
        removeImageButton = findViewById(R.id.removeImageButton)

        imageUrl = intent.getStringExtra("IMAGE_URL")

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.no_internet_connection)
                .into(imageView)
        }
        removeImageButton.setOnLongClickListener {
            Toast.makeText(this, "Delete Profile Image", Toast.LENGTH_SHORT).show()
            true
        }
        removeImageButton.setOnClickListener {
            removeProfileImage()
        }
    }

    private fun removeProfileImage() {
        val userId = auth.currentUser?.uid ?: return
        val imagePath = "profile_images/$userId.jpg"
        val profileImageRef = storageRef.child(imagePath)

        profileImageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Profile photo removed", Toast.LENGTH_SHORT).show()
                // Return to the previous activity or update UI
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove image", Toast.LENGTH_SHORT).show()
            }
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
