package com.cmc.aqualis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RecordAdapter(private val context: Context, private val records: List<Int>) : BaseAdapter() {
    override fun getCount(): Int = records.size
    override fun getItem(position: Int): Any = records[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${records[position]} ml"
        return view
    }
}