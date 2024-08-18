package com.example.projectone.manufacturer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class ManufacturerNotifications : AppCompatActivity() {
    private lateinit var toolbarManufacturerNotifications:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manufacturer_notifications)
        toolbarManufacturerNotifications=findViewById(R.id.toolbarManufacturerNotifications)
        setSupportActionBar(toolbarManufacturerNotifications)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Notifications"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}