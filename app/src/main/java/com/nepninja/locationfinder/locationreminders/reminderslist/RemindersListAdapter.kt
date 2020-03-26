package com.nepninja.locationfinder.locationreminders.reminderslist

import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.base.BaseRecyclerViewAdapter
import com.nepninja.locationfinder.locationreminders.reminderslist.ReminderDataItem


//Use data binding to show the reminder on the item
class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}