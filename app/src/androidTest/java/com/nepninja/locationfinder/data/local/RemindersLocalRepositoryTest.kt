package com.nepninja.locationfinder.data.local

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.data.ReminderDataSource
import com.nepninja.locationfinder.data.dto.ReminderDTO
import com.nepninja.locationfinder.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest : ReminderDataSource {
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
    private var reminders = listOf(servicingReminder, bookPurchaseReminder)
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
        return Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.toMutableList().add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error(
                ApplicationProvider.getApplicationContext<Application>()
                    .getString(R.string.err_reminder)
            )
        }
        return Result.Success(reminders.filter { it.id == id } as ReminderDTO)
    }

    override suspend fun deleteAllReminders() {
        reminders.toMutableList().clear()
    }
}