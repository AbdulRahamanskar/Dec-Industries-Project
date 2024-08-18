package com.example.projectone.head

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class SalesHeadFeedback : AppCompatActivity() {
    private lateinit var toolbarSalesHeadFeedbackForm:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_feedback)
        toolbarSalesHeadFeedbackForm=findViewById(R.id.toolbarSalesHeadFeedbackForm)
        setSupportActionBar(toolbarSalesHeadFeedbackForm)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Feedback"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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