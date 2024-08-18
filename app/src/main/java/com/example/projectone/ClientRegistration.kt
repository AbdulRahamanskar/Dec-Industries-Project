package com.example.projectone

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.content.ContextCompat
import com.example.projectone.client.ClientProjects
import com.example.projectone.client.clientdatamodels.Client
//import com.example.projectone.salesperson.SalesPersonCreateOrder
import com.example.projectone.salesperson.SalesPersonProjects
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ClientRegistration : AppCompatActivity() {
    // Authentication and database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar

    private lateinit var clientSignUpBtn: Button
    private lateinit var autoCompleteGender: AutoCompleteTextView
    private lateinit var clientFullname: TextInputEditText
    private lateinit var clientEmail: TextInputEditText
    private lateinit var clientPassword: TextInputEditText
    private lateinit var clientConfirmPassword: TextInputEditText
    private lateinit var clientMobile: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_registration)

        // Initialize auth and database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Views
        progressBar = findViewById(R.id.progressBar)
        clientEmail = findViewById(R.id.etClientEmail)
        clientFullname = findViewById(R.id.etClientFullName)
        clientPassword = findViewById(R.id.etClientPassword)
        clientConfirmPassword = findViewById(R.id.etClientConfirmPassword)
        clientMobile = findViewById(R.id.etClientMobile)
        autoCompleteGender = findViewById(R.id.autoCompleteClientGender)

        // Client signup button
        clientSignUpBtn = findViewById(R.id.btnClientLogin)
        clientSignUpBtn.setOnClickListener {
            val fullname = clientFullname.text.toString().trim()
            val email = clientEmail.text.toString().trim()
            val password = clientPassword.text.toString().trim()
            val confirmPassword = clientConfirmPassword.text.toString().trim()
            val mobile = clientMobile.text.toString().trim()
            val gender = autoCompleteGender.text.toString().trim()

            // Show ProgressBar and disable the button
            showLoading(true)

            // Validate form
            if (validateForm(fullname, email, password, confirmPassword, mobile, gender)) {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                userId?.let {
                                    generateClientIdAndSaveUser(fullname, email, mobile, gender, it)
                                } ?: run {
                                    showLoading(false)
                                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                showLoading(false)
                                Toast.makeText(this, "Failed to register: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                showLoading(false)
            }
        }

        // Status bar color change
        window.statusBarColor = ContextCompat.getColor(this, R.color.bottom_bg_color)

        // Gender dropdown setup
        val genderArray = arrayOf("Male", "Female", "Others")
        val genderArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderArray)
        autoCompleteGender.setAdapter(genderArrayAdapter)
        autoCompleteGender.setDropDownBackgroundResource(R.drawable.list_bottomsheetbg)

        // Login screen redirect
        val tvLogin = findViewById<TextView>(R.id.tvClientLogin)
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(findViewById(R.id.tilClientEmail), "transition_email"),
                Pair(findViewById(R.id.tilClientPassword), "transition_password"),
                Pair(findViewById(R.id.tvClientLogin), "tvlogintransition")
            ).toBundle()
            startActivity(intent, options)
        }
    }

    private fun validateForm(
        fullname: String,
        email: String,
        password: String,
        confirmPassword: String,
        mobile: String,
        gender: String
    ): Boolean {
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            mobile.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this@ClientRegistration, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password should be at least 7 characters long, contain a mix of uppercase and lowercase letters, numbers, and special characters", Toast.LENGTH_LONG).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this@ClientRegistration, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isPasswordStrong(password: String): Boolean {
        if (password.length <= 7) return false
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialCharacter = password.any { it in "!@#$%^&*()_-+=?/|" }
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialCharacter
    }

    private fun generateClientId(fullname: String, existingIds: Set<String>): String {
        val firstTwoLetters = fullname.take(2).uppercase()
        var clientId: String
        do {
            val randomFiveDigitNumber = (10000..99999).random() // Generates a random 5-digit number
            clientId = "$firstTwoLetters$randomFiveDigitNumber"
        } while (existingIds.contains(clientId)) // Ensure the ID is unique

        return clientId
    }

    private fun generateClientIdAndSaveUser(fullname: String, email: String, mobile: String, gender: String, userId: String) {
        val usersRef = database.child("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingIds = mutableSetOf<String>()
                for (userSnapshot in snapshot.children) {
                    for (roleSnapshot in userSnapshot.children) {
                        val clientId = roleSnapshot.child("clientId").getValue(String::class.java)
                        if (clientId != null) {
                            existingIds.add(clientId)
                        }
                    }
                }

                val clientId = generateClientId(fullname, existingIds)
                val currentYear= java.time.Year.now().value.toString()//current year
                val currentDate=java.time.LocalDate.now() //get the current date
                val dateFormatter=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = currentDate.format(dateFormatter) // Format date as dd/MM/yyyy

                val newClient = Client(
                    fullname = fullname,
                    email = email,
                    clientId = clientId,
                    role = "client",
                    doj = formattedDate,
                    companyName = "Add details from edit profile section",
                    since = currentYear,
                    completedProjects = "0",
                    highestQualification = "Add details from edit profile section",
                    skills = "Add details from edit profile section",
                    experience = "Add details from edit profile section",
                    mobile = mobile,
                    gender = gender,
                    languageKnown = "Add details from edit profile section"
                )

                usersRef.child(userId).setValue(newClient).addOnCompleteListener { dbTask ->
                    showLoading(false)
                    if (dbTask.isSuccessful) {
                        Toast.makeText(this@ClientRegistration, "Registration Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ClientRegistration, ClientProjects::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@ClientRegistration, "Failed to store user data", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoading(false)
                Toast.makeText(this@ClientRegistration, "Failed to fetch existing IDs: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        clientSignUpBtn.isEnabled = !isLoading
    }
}
//
//data class Client(
//    val fullname: String = "",
//    val email: String = "",
//    val clientId: String = "",
//    val role: String = "",
//    val doj: String = "",
//    val companyName: String = "",
//    val since: String = "",
//    val completedProjects: String = "",
//    val highestQualification: String = "",
//    val skills: String = "",
//    val experience: String = "",
//    val mobile: String = "",
//    val gender: String = "",
//    val languageKnown:String=""
//    )
