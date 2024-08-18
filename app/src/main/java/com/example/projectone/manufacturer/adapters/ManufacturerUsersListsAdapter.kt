package com.example.projectone.manufacturer.adapters

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
import com.example.projectone.manufacturer.manufacturerdatamodels.Manufacturer
import com.google.android.material.imageview.ShapeableImageView

class ManufacturerUsersListsAdapter(context: Context,
  private val originalManufacturersList:List<Manufacturer>):
    ArrayAdapter<Manufacturer>(context,0,originalManufacturersList), Filterable {
    private var filteredManufacturers=originalManufacturersList
    private val manufacturerFilter=ManufacturerFilter()


    override fun getFilter(): Filter {
        return manufacturerFilter
    }

    override fun getCount(): Int {
        return filteredManufacturers.size
    }

    override fun getItem(position: Int): Manufacturer? {
        return filteredManufacturers.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.adminmanufacturersuserslists, parent, false)
        val manufactureres=getItem(position)
        manufactureres?.let {
            val manufacturerProfileImage=view.findViewById<ShapeableImageView>(R.id.adminManufacturerProfileImage)
            val manufacturerFullName=view.findViewById<TextView>(R.id.adminManufacturerFullName)
            val manufacturerRole=view.findViewById<TextView>(R.id.adminManufacturerRole)
            val manufacturerEmpId=view.findViewById<TextView>(R.id.adminManufacturerEmpId)
            val manufacturerEmail=view.findViewById<TextView>(R.id.adminManufacturerEmail)
            val manufacturerDoj=view.findViewById<TextView>(R.id.adminManufacturerDoj)
            val manufacturerDelete=view.findViewById<ImageButton>(R.id.adminManufacturerDeleteImageButton)

            manufacturerFullName.text = "Name: ${it.fullname}".ifEmpty { "No Full Name" }
            manufacturerRole.text = "Role: ${it.role}".ifEmpty { "No Role" }
            manufacturerEmpId.text = "EmpId: ${it.empId}".ifEmpty { "No Emp ID" }
            manufacturerEmail.text = "Email: ${it.email}".ifEmpty { "No Email" }
            manufacturerDoj.text = "Joined: ${it.doj}".ifEmpty { "No Doj" }

            // Load profile image
            Glide.get(context).clearMemory()
            if (it.profileImageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.sample_logo) // Placeholder image if URL is empty or loading
                    .error(R.drawable.sample_logo) // Error image if loading fails
                    .into(manufacturerProfileImage as ImageView)
            } else {
                manufacturerProfileImage.setImageResource(R.drawable.sample_logo) // Use a default image if URL is empty
            }
        }
        return view
    }
    private inner class ManufacturerFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            val query = constraint?.toString()?.lowercase() ?: ""
            val filteredList = if (query.isEmpty()) {
                originalManufacturersList
            } else {
                originalManufacturersList.filter {
                    it.fullname.lowercase().contains(query) || it.empId.lowercase().contains(query)
                }
            }

            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredManufacturers = results?.values as List<Manufacturer>? ?: emptyList()
            notifyDataSetChanged()
        }
    }

}