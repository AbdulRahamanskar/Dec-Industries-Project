package com.example.projectone.salesperson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.client.clientdatamodels.Client
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.imageview.ShapeableImageView

class SalesPersonUserListAdapter (context:Context,
 private val originalSalesPersonSList:List<SalesPerson>
):ArrayAdapter<SalesPerson>(context,0,originalSalesPersonSList),Filterable{
    private var filteredSalesPerson=originalSalesPersonSList
    private val salesPersonFilter=SalesPersonFilter()

    override fun getFilter(): Filter {
        return salesPersonFilter
    }

    override fun getCount(): Int {
        return filteredSalesPerson.size
    }

    override fun getItem(position: Int): SalesPerson? {
        return filteredSalesPerson.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.adminsalespersonsuserslists, parent, false)
        val salesPerson=getItem(position)
        salesPerson?.let {
            val salespersonProfileImage=view.findViewById<ShapeableImageView>(R.id.adminSalesPersonProfileImage)
            val salesPersonFullName=view.findViewById<TextView>(R.id.adminSalesPersonFullName)
            val salesPersonRole=view.findViewById<TextView>(R.id.adminSalesPersonRole)
            val salesPersonEmpId=view.findViewById<TextView>(R.id.adminSalesPersonEmpId)
            val salesPersonEmail=view.findViewById<TextView>(R.id.adminSalesPersonEmail)
            val salesPersonDoj=view.findViewById<TextView>(R.id.adminSalesPersonDoj)
            val salesPersonDelete=view.findViewById<ImageButton>(R.id.adminSalesPersonDeleteImageButton)

            salesPersonFullName.text = "Name: ${it.fullname}".ifEmpty { "No Full Name" }
            salesPersonRole.text = "Role:${it.role}".ifEmpty { "No Role" }
            salesPersonEmpId.text = "EmpId: ${it.empId}".ifEmpty { "No Emp ID" }
            salesPersonEmail.text = "Email: ${it.email}".ifEmpty { "No Email" }
            salesPersonDoj.text = "Joined: ${it.doj}".ifEmpty { "No Doj" }

            // Load profile image
            Glide.get(context).clearMemory()
            if (it.profileImageUrl?.isNotEmpty() == true) {
                Glide.with(context)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.sample_logo) // Placeholder image if URL is empty or loading
                    .error(R.drawable.sample_logo) // Error image if loading fails
                    .into(salespersonProfileImage as ImageView)
            } else {
                salespersonProfileImage.setImageResource(R.drawable.sample_logo) // Use a default image if URL is empty
            }
        }
        return view
    }
    private inner class SalesPersonFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            val query = constraint?.toString()?.lowercase() ?: ""
            val filteredList = if (query.isEmpty()) {
                originalSalesPersonSList
            } else {
                originalSalesPersonSList.filter {
                    it.fullname.lowercase().contains(query) || it.empId.lowercase().contains(query)
                }
            }

            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredSalesPerson = results?.values as List<SalesPerson>? ?: emptyList()
            notifyDataSetChanged()
        }
    }

}