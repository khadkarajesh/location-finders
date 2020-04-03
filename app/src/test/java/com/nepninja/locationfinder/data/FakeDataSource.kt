package com.nepninja.locationfinder.data

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.data.dto.ReminderDTO
import com.nepninja.locationfinder.data.dto.Result
import com.nepninja.locationfinder.reminderslist.ReminderDataItem

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
    private val servicingReminder = ReminderDTO(
        "Car servicing",
        "Servicing Car at Tesla's garage",
        "New Jersey", 27.2299,
        48.00029
    )
    private val bookPurchaseReminder = ReminderDTO(
        "Buy Book",
        "Buy series of all the boys i loved before",
        "Peoples plaza",
        27.2299,
        48.00029
    )


    var reminders = listOf(
        servicingReminder,
        bookPurchaseReminder
    ).toMutableList()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                ApplicationProvider.getApplicationContext<Application>()
                    .getString(R.string.err_reminder)
            )
        }
        return Result.Success(reminders.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error(
                ApplicationProvider.getApplicationContext<Application>()
                    .getString(R.string.err_reminder)
            )
        }
        return Result.Success(servicingReminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun dtoToPojo(dataItems: List<ReminderDTO>): List<ReminderDataItem> {
        val dataList = ArrayList<ReminderDataItem>()
        dataList.addAll(dataItems.map { reminder ->
            //map the reminder data from the DB to the be ready to be displayed on the UI
            ReminderDataItem(
                reminder.title,
                reminder.description,
                reminder.location,
                reminder.latitude,
                reminder.longitude,
                reminder.id
            )
        })
        return dataList
    }


}