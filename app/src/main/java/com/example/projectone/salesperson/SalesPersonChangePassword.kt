package com.example.projectone.salesperson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SalesPersonChangePassword : AppCompatActivity() {
    private lateinit var lastPassword:TextInputEditText
    private lateinit var newPassword:TextInputEditText
    private lateinit var confirmNewPassword:TextInputEditText
    private lateinit var auth:FirebaseAuth
    private lateinit var updateSalesPersonChangePassword:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_change_password)
        //views
        lastPassword=findViewById(R.id.etSalesLastPassword)
        newPassword=findViewById(R.id.etSalesNewPassword)
        confirmNewPassword=findViewById(R.id.etSalesConfirmNewPassword)
        updateSalesPersonChangePassword=findViewById(R.id.updateSalesPersonChangePassword)
        auth=FirebaseAuth.getInstance()
        val salesChangePasswordToolbar=findViewById<Toolbar>(R.id.salesToolbarChangePassword)
        setSupportActionBar(salesChangePasswordToolbar)

        supportActionBar?.apply {
            supportActionBar?.title="Change Password"
            supportActionBar?.title=""
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        updateSalesPersonChangePassword.setOnClickListener {
            changePassword()
        }

    }

    private fun changePassword() {
        val currentPassword = lastPassword.text.toString().trim()
        val newPassword = newPassword.text.toString().trim()
        val confirmPassword = confirmNewPassword.text.toString().trim()

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
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}