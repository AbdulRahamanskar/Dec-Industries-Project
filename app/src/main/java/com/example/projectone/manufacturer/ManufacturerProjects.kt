package com.example.projectone.manufacturer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManufacturerProjects : AppCompatActivity() {
    private lateinit var manufacturerProjectsBottom:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manufacturer_projects)
        val toolbarManufacturerProjects=findViewById<Toolbar>(R.id.toolbarManufacturerProjects)
        setSupportActionBar(toolbarManufacturerProjects)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Projects"
        manufacturerProjectsBottom=findViewById(R.id.manufacturerProjectsBottom)
        manufacturerProjectsBottom.selectedItemId=R.id.manufacturerProjectsMenu
        manufacturerProjectsBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manufacturer_notifications,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.manufacturerNotificationsMenu->{
                startActivity(Intent(this,ManufacturerNotifications::class.java))
                return true
            }
            else ->super.onOptionsItemSelected(item)

        }
    }

    private fun handleNavigation(itemId: Int):Boolean {
        if (itemId==manufacturerProjectsBottom.selectedItemId){
            return true
        }
        when(itemId){
            R.id.manufacturerProjectsMenu->{
                return true
            }
            R.id.manufacturerProfileMenu->{
                startActivity(Intent(this@ManufacturerProjects,ManufacturerProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.manufacturerSettingsMenu->{
                startActivity(Intent(this@ManufacturerProjects,ManufacturerSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else ->return false

        }

    }
}