package com.example.projectone.salesperson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class SalesPersonFeedbackLists : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_feedback_lists)
        val toolbarSalesPersonsFeedbackLists=findViewById<Toolbar>(R.id.toolbarSalesPersonFeedbackLists)
        setSupportActionBar(toolbarSalesPersonsFeedbackLists)
        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle="Feedback Lists"
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

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