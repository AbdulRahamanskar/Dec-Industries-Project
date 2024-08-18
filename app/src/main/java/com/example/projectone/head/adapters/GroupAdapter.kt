package com.example.projectone.head.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.head.salesheaddatamodels.Group
import com.example.projectone.salesperson.adapters.OrderAdapter
import com.google.android.material.imageview.ShapeableImageView

class GroupAdapter(private val context: Context, private val groups: List<Group>,
//                   private val itemClickListener: onDeleteGroupsListner

) : BaseAdapter() {

    override fun getCount(): Int = groups.size

    override fun getItem(position: Int): Any = groups[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)
        val group = getItem(position) as Group

        val groupNameTextView = view.findViewById<TextView>(R.id.groupName)
        val groupImage = view.findViewById<ShapeableImageView>(R.id.groupImage)
        val createdByTextView = view.findViewById<TextView>(R.id.groupCreatedBy)
        val empIdTextView = view.findViewById<TextView>(R.id.salesHeadEmpId)
        val tvDeleteGroup = view.findViewById<TextView>(R.id.tvDeleteGroup)

        groupNameTextView.text = group.name
        createdByTextView.text = group.salesHeadName
        empIdTextView.text = group.salesHeadId

        // Load images using Glide or any other image loading library
        Glide.with(context)
            .load(group.imageUrl)
            .placeholder(R.drawable.sample_logo)  // Optional placeholder
//            .error(R.drawable.wait)
            .into(groupImage)
//        tvDeleteGroup.setOnClickListener {
//            Toast.makeText(context, "Delete Group clicked...", Toast.LENGTH_SHORT).show()
//            group.id?.let { it1 -> itemClickListener.onDeleteGroup(it1) }
//
//        }

        return view
    }
//    interface onDeleteGroupsListner {
//        fun onDeleteGroup(groupId:String)
//
//    }
}
