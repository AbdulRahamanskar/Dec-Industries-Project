package com.example.projectone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.projectone.admin.AdminOngoingProjects
import com.example.projectone.client.ClientProjects
import com.example.projectone.head.SalesHeadProjects
import com.example.projectone.manufacturer.ManufacturerProjects
import com.example.projectone.salesperson.SalesPersonProjects
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginScreen : AppCompatActivity() {
    private lateinit var etEmail:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var progressBar: ProgressBar

    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance().reference

        etEmail=findViewById(R.id.etEmail)
        etPassword=findViewById(R.id.etPassword)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
//        val tvClientTemp = findViewById<TextView>(R.id.tvClientTemp)
//        val tvSalesHeadTemp = findViewById<TextView>(R.id.tvSalesHeadTemp)
//        val tvManufacturerTemp = findViewById<TextView>(R.id.tvManufacturerTemp)
        progressBar = findViewById(R.id.progressBar)

//        tvManufacturerTemp.setOnClickListener {
//            startActivity(Intent(this, ManufacturerProjects::class.java))
//        }
//        tvSalesHeadTemp.setOnClickListener {
//            startActivity(Intent(this, SalesHeadProjects::class.java))
//        }
//
//        tvClientTemp.setOnClickListener {
//            startActivity(Intent(this, ClientProjects::class.java))
//        }
//        //check if user is already logged in
//        if (auth.currentUser!=null){
//            reDirectToHomePage(auth.currentUser!!.uid)
//        }

        btnLogin.setOnClickListener {
//            startActivity(Intent(this, AdminOngoingProjects::class.java))
            val email=etEmail.text.toString().trim()
            val password=etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Show the progress bar and hide the login button
            progressBar.visibility = ProgressBar.VISIBLE
            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // Hide the progress bar and show the login button
                progressBar.visibility = ProgressBar.GONE
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                        reDirectToHomePage(userId)
                    } else {
                        Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }




        }

        tvForgot.setOnClickListener {
            val intent=Intent(this,ForgotPassword::class.java)
            val options= ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(findViewById(R.id.tilEmail),"transition_email"),
                Pair(findViewById(R.id.tvForgot),"forgotPassword_transition")
            ).toBundle()
            startActivity(intent,options)
        }

        tvSignUp.setOnClickListener {
            // Replace with your desired destination activity
            val intent = Intent(this, ClientRegistration::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(findViewById(R.id.tilEmail), "transition_email"),
                Pair(findViewById(R.id.tilPassword), "transition_password")
            ).toBundle()
            startActivity(intent, options)
        }
    }

    //fns
    @SuppressLint("SuspiciousIndentation")
    private fun reDirectToHomePage(userId:String){
        val userRef=database.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRole=snapshot.child("role").getValue(String::class.java)
                    val intent=when(userRole){
                        "admin"->Intent(this@LoginScreen,AdminOngoingProjects::class.java)
                        "client"->Intent(this@LoginScreen,ClientProjects::class.java)
                        "manufacturer"->Intent(this@LoginScreen,ManufacturerProjects::class.java)
                        "saleshead"->Intent(this@LoginScreen,SalesHeadProjects::class.java)
                        "salesperson"->Intent(this@LoginScreen,SalesPersonProjects::class.java)
                        else -> {
                            Toast.makeText(this@LoginScreen, "Unknown role", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginScreen, "Failed to fetch user role: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
