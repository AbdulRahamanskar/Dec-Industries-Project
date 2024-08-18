package com.example.projectone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.projectone.admin.AdminOngoingProjects
import com.example.projectone.client.ClientProjects
import com.example.projectone.head.SalesHeadProjects
import com.example.projectone.manufacturer.ManufacturerProjects
import com.example.projectone.salesperson.SalesPersonProjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import androidx.core.util.Pair

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoSplash = findViewById<CircleImageView>(R.id.ivLogo)
        val animbounce = AnimationUtils.loadAnimation(this, R.anim.combo)
        animbounce.duration = 1500
        logoSplash.startAnimation(animbounce)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        if (NetworkUtils.isNetworkAvailable(this)) {
            Handler(Looper.getMainLooper()).postDelayed({
                handleUserNavigation()
            }, 2000)
        } else {
            showNoInternetImage()
        }
    }

    private fun showNoInternetImage() {
        val logoSplash = findViewById<CircleImageView>(R.id.ivLogo)
        logoSplash.setImageResource(R.drawable.no_internet_connection) // Replace with your no internet image
        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
    }

    private fun handleUserNavigation() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserRole(currentUser.uid)
        } else {
            redirectToLoginScreen()
        }
    }

    private fun checkUserRole(uid: String) {
        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRole = snapshot.child("role").getValue(String::class.java)

                when (userRole) {
                    "client" -> redirectToClientProjects()
                    "admin" -> redirectToAdminScreen()
                    "manufacturer" -> redirectToManufacturerScreen()
                    "saleshead" -> redirectToSalesHeadScreen()
                    "salesperson" -> redirectToSalesPersonScreen()
                    else -> redirectToLoginScreen()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to read user role", Toast.LENGTH_SHORT).show()
                redirectToLoginScreen()
            }
        })
    }

    private fun redirectToClientProjects() {
        startActivity(Intent(this, ClientProjects::class.java))
        finish()
    }

    private fun redirectToAdminScreen() {
        startActivity(Intent(this, AdminOngoingProjects::class.java))
        finish()
    }

    private fun redirectToManufacturerScreen() {
        startActivity(Intent(this, ManufacturerProjects::class.java))
        finish()
    }

    private fun redirectToSalesPersonScreen() {
        startActivity(Intent(this, SalesPersonProjects::class.java))
        finish()
    }

    private fun redirectToSalesHeadScreen() {
        startActivity(Intent(this, SalesHeadProjects::class.java))
        finish()
    }

    private fun redirectToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(findViewById(R.id.ivLogo), "splash_logo_transition")
        ).toBundle()
        startActivity(intent, options)
        finish()
    }
}
