package com.example.projectone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val toolbarAboutUs=findViewById<Toolbar>(R.id.toolbarAboutUs)

        setSupportActionBar(toolbarAboutUs)
        supportActionBar?.apply {
            supportActionBar?.subtitle="About Us"
            supportActionBar?.title=""
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
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