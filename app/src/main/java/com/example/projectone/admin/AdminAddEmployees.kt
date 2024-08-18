package com.example.projectone.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.projectone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.random.Random

class AdminAddEmployees : AppCompatActivity() {

    private lateinit var progressbar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etAddEmpFullName: TextInputEditText
    private lateinit var etAddEmpEmail: TextInputEditText
    private lateinit var etAddEmpPassword: TextInputEditText
    private lateinit var etAddEmpMobile: TextInputEditText
    private lateinit var etAddEmpID: TextInputEditText
    private lateinit var etAddEmpDOB: TextInputEditText
    private lateinit var etAddEmpDOJ: TextInputEditText
    private lateinit var tilAddEmpID: TextInputLayout

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var autoCompleteAddEmpGender: AutoCompleteTextView
    private lateinit var autoCompleteAddEmpPosition: AutoCompleteTextView
    private lateinit var autoCompleteAddEmpDepartment: AutoCompleteTextView

    private var isEmpIdGenerated = false // To track if empId is generated

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_employees)

        // Initialize Firebase, SharedPreferences, and views
        progressbar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)

        etAddEmpFullName = findViewById(R.id.etAddEmpFullName)
        etAddEmpEmail = findViewById(R.id.etAddEmpEmail)
        etAddEmpPassword = findViewById(R.id.etAddEmpPassword)
        etAddEmpMobile = findViewById(R.id.etAddEmpMobile)
        etAddEmpID = findViewById(R.id.etAddEmpID)
        etAddEmpDOB = findViewById(R.id.etAddEmpDOB)
        etAddEmpDOJ = findViewById(R.id.etAddEmpDOJ)
        tilAddEmpID = findViewById(R.id.tilAddEmpID)

        autoCompleteAddEmpDepartment = findViewById(R.id.autoCompleteAddEmpDepartment)
        autoCompleteAddEmpGender = findViewById(R.id.autoCompleteAddEmpGender)
        autoCompleteAddEmpPosition = findViewById(R.id.autoCompleteAddEmpPosition)

        val btnAddtoDB = findViewById<Button>(R.id.btnAddtoDB)

        // Generate unique empId when fullname is entered
        etAddEmpFullName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isEmpIdGenerated) {
                val fullname = etAddEmpFullName.text.toString().trim()
                if (fullname.isNotEmpty()) {
                    generateUniqueEmpId(fullname) { empId ->
                        etAddEmpID.setText(empId)
                        sharedPreferences.edit { putString("empId", empId) }
                        isEmpIdGenerated = true // Set flag to true
                    }
                }
            }
        }

        // Handle adding employee to the database
        btnAddtoDB.setOnClickListener {
            val fullname = etAddEmpFullName.text.toString().trim()
            val email = etAddEmpEmail.text.toString().trim()
            val password = etAddEmpPassword.text.toString().trim()
            val mobile = etAddEmpMobile.text.toString().trim()
            val empId = etAddEmpID.text.toString().trim()
            val doj = etAddEmpDOJ.text.toString().trim()
            val dob = etAddEmpDOB.text.toString().trim()
            val gender = autoCompleteAddEmpGender.text.toString().trim()
            val department = autoCompleteAddEmpDepartment.text.toString().trim()
            val position = autoCompleteAddEmpPosition.text.toString().trim()

            if (validateForm(fullname, email, password, mobile, doj, dob, gender, department, position, empId)) {
                showLoading(true)

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        showLoading(false)
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                val userRef = database.child("users").child(userId)
                                val userMap = mapOf(
                                    "fullname" to fullname,
                                    "email" to email,
                                    "password" to password,
                                    "mobile" to mobile,
                                    "gender" to gender,
                                    "role" to position,
                                    "department" to department,
                                    "dob" to dob,
                                    "doj" to doj,
                                    "empId" to empId
                                )
                                userRef.setValue(userMap)
                                    .addOnCompleteListener { userTask ->
                                        if (userTask.isSuccessful) {
                                            Toast.makeText(this, "Employee added successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Failed to add employee: ${userTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                return@setOnClickListener
            }
            showLoading(false)
        }

        // Set up toolbar and auto-complete fields
        val toolbarAddEmp = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_addemployees)
        setSupportActionBar(toolbarAddEmp)
        supportActionBar?.title = ""
        supportActionBar?.setSubtitle("Add Employees")

        val departmentArray = arrayOf("Sales", "Manufacturer")
        val departmentArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, departmentArray)
        autoCompleteAddEmpDepartment.setAdapter(departmentArrayAdapter)
        autoCompleteAddEmpDepartment.setDropDownBackgroundResource(R.drawable.list_bottomsheetbg)

        val genderArray = arrayOf("Male", "Female")
        val genderArrayAddEmpAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genderArray)
        autoCompleteAddEmpGender.setAdapter(genderArrayAddEmpAdapter)
        autoCompleteAddEmpGender.setDropDownBackgroundResource(R.drawable.list_bottomsheetbg)

        val positionArray = arrayOf("salesperson", "saleshead", "manufacturer")
        val positionArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, positionArray)
        autoCompleteAddEmpPosition.setAdapter(positionArrayAdapter)
        autoCompleteAddEmpPosition.setDropDownBackgroundResource(R.drawable.list_bottomsheetbg)

        bottomNav = findViewById(R.id.adminBottomNav)
        bottomNav.selectedItemId = R.id.adminaddAddEmployeesMenu

        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
        }
    }

    private fun generateUniqueEmpId(fullname: String, callback: (String) -> Unit) {
        val prefix = if (fullname.length >= 2) fullname.take(2).toUpperCase() else fullname.toUpperCase()
        val usersRef = database.child("users")

        fun checkAndGenerateEmpId() {
            val empId = generateRandomEmpId(prefix)
            usersRef.orderByChild("empId").equalTo(empId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    checkAndGenerateEmpId()
                } else {
                    callback(empId)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to check employee ID existence: ${exception.message}", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
        }

        checkAndGenerateEmpId()
    }

    private fun generateRandomEmpId(prefix: String): String {
        val randomDigits = Random.nextInt(10000, 99999).toString().padStart(5, '0')
        return "$prefix$randomDigits"
    }

    private fun showLoading(isLoading: Boolean) {
        progressbar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    private fun validateForm(
        fullname: String,
        email: String,
        password: String,
        mobile: String,
        doj: String,
        dob: String,
        gender: String,
        department: String,
        position: String,
        empId: String
    ): Boolean {
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || mobile.isEmpty() ||
            doj.isEmpty() || dob.isEmpty() || gender.isEmpty() || department.isEmpty() ||
            position.isEmpty() || empId.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isStrongPassword(password)) {
            Toast.makeText(this, "Password should be at least 7 characters long, contain a mix of uppercase and lowercase letters, numbers, and special characters", Toast.LENGTH_LONG).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isStrongPassword(password: String): Boolean {
        if (password.length <= 7) return false
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigits = password.any { it.isDigit() }
        val hasSpecialCharacters = password.any { it in "!@#_." }
        return hasDigits && hasLowerCase && hasUpperCase && hasSpecialCharacters
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
            R.id.adminaddAddEmployeesMenu -> true
            R.id.adminManufactringInfoMenu -> {
                startActivity(Intent(this, AdminManufacturerInfo::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
            R.id.adminProfileMenu -> {
                startActivity(Intent(this, AdminProfile::class.java))
                overridePendingTransition(0, 0)
                finish()
                true
            }
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
