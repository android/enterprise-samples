/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.workprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Contact(val name: String, val isWork: Boolean)

class ContactsAdapter(private val contactList: MutableList<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ContactViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.contact_item_layout, viewGroup, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.contact?.let {
            it.text = contact.name
            it.setCompoundDrawablesRelativeWithIntrinsicBounds(
                it.resources?.getDrawable(
                    when (contact.isWork) {
                        true -> R.drawable.ic_person_primary_40dp
                        false -> R.drawable.ic_person_green_40dp
                    },
                    null
                ),
                null,
                null,
                null
            )
        }
    }

    override fun getItemCount(): Int = contactList.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contact: TextView? = itemView.findViewById(R.id.contact_name_tv) as TextView
    }
}
