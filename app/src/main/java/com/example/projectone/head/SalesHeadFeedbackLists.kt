package com.example.projectone.head

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class SalesHeadFeedbackLists : AppCompatActivity() {
    private lateinit var toolbarSalesHeadFeedbackLists:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_feedback_lists)
        toolbarSalesHeadFeedbackLists=findViewById(R.id.toolbarSalesHeadFeedbackLists)
        setSupportActionBar(toolbarSalesHeadFeedbackLists)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Feedback Lists"
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