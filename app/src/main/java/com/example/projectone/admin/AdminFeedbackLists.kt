package com.example.projectone.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class AdminFeedbackLists : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_feedback_lists)

        val toolbarAdminFeedbackLists=findViewById<Toolbar>(R.id.AdminToolbarFeedbackLists)
        setSupportActionBar(toolbarAdminFeedbackLists)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.subtitle="Feedback Lists"
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