package com.example.projectone.client

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

class ClientChangePassword : AppCompatActivity() {
    private lateinit var lastPassword:TextInputEditText
    private lateinit var newPassword:TextInputEditText
    private lateinit var newConfirmPassword:TextInputEditText
    private lateinit var btnChangePassword:Button
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_change_password)

        auth=FirebaseAuth.getInstance()
        lastPassword=findViewById(R.id.etClientLastPassword)
        newPassword=findViewById(R.id.etClientNewPassword)
        newConfirmPassword=findViewById(R.id.etClientConfirmNewPassword)
        btnChangePassword=findViewById(R.id.btnClientUpdatePassword)
        val toolbarClientChangePassword=findViewById<Toolbar>(R.id.toolbar_client_changepassword)
        setSupportActionBar(toolbarClientChangePassword)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.subtitle="Change Password"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        btnChangePassword.setOnClickListener {
            changePassword()

        }
    }
    private fun changePassword() {
        val currentPassword = lastPassword.text.toString().trim()
        val newPassword = newPassword.text.toString().trim()
        val confirmPassword = newConfirmPassword.text.toString().trim()

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