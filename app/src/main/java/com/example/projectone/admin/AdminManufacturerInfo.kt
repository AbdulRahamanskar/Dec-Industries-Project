package com.example.projectone.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminManufacturerInfo : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manufacturer_info)

        val toolbarManufacturerInfo=findViewById<Toolbar>(R.id.toolbar_manufacturerInfo)

        setSupportActionBar(toolbarManufacturerInfo)
        supportActionBar?.title=""
        supportActionBar?.apply {
            setSubtitle("Manufacturer Info")
        }

        bottomNav = findViewById(R.id.adminManufacturerInfoBottomNav) // Corrected to use R.id.adminBottomNav

        // Set the default selected item
        bottomNav.selectedItemId = R.id.adminManufactringInfoMenu // Select the manufacturer menu item


        bottomNav.setOnNavigationItemSelectedListener {menuItem ->
            handleNavigation(menuItem.itemId)

        }

    }

    private fun handleNavigation(itemId: Int): Boolean {
        // Check if the current activity is already the selected one
        if (itemId == bottomNav.selectedItemId) {
            return true
                //activity should be remains same
        }

        when (itemId) {
            R.id.adminOngoingProjectsMenu -> {
                startActivity(Intent(this, AdminOngoingProjects::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminaddAddEmployeesMenu -> {
                startActivity(Intent(this, AdminAddEmployees::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminManufactringInfoMenu -> {
                // No action needed as we are already in this activity
                return true
            }
            R.id.adminProfileMenu -> {
                startActivity(Intent(this, AdminProfile::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminMoreMenu -> {
                startActivity(Intent(this, AdminMoreSettings::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            else -> return false
        }
    }


}
