package com.example.projectone.head


import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class SalesHeadChangePassword : AppCompatActivity() {
    private lateinit var lastPasswordET: TextInputEditText
    private lateinit var newPasswordET: TextInputEditText
    private lateinit var newConfirmPasswordET: TextInputEditText
    private lateinit var btnChangePassword: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_change_password)

        lastPasswordET = findViewById(R.id.etSalesHeadLastPassword)
        newPasswordET = findViewById(R.id.etSalesHeadNewPassword)
        newConfirmPasswordET = findViewById(R.id.etSalesHeadConfirmNewPassword)
        btnChangePassword = findViewById(R.id.btnSalesHeadChangePassword)
        val adminToolbarChangePassword = findViewById<Toolbar>(R.id.salesHeadToolbarChangePassword)

        setSupportActionBar(adminToolbarChangePassword)
        supportActionBar?.apply {
            title = ""
            subtitle = "Change Password"
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        }

        auth = FirebaseAuth.getInstance()

        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val currentPassword = lastPasswordET.text.toString().trim()
        val newPassword = newPasswordET.text.toString().trim()
        val confirmPassword = newConfirmPasswordET.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "New password and confirmation do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        user?.let {
            // Re-authenticate the user
            val credential = EmailAuthProvider.getCredential(it.email!!, currentPassword)
            it.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update password
                    it.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        } else {
                            Toast.makeText(this, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to re-authenticate: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}