package com.example.projectone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        val toolbarPrivacyPolicy=findViewById<Toolbar>(R.id.toolbarPrivacyPolicy)

        setSupportActionBar(toolbarPrivacyPolicy)
        supportActionBar?.apply {
            supportActionBar?.subtitle="Privacy Policy"
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