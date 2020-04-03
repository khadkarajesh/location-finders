package com.nepninja.locationfinder.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.data.ReminderDataSource
import com.nepninja.locationfinder.data.dto.ReminderDTO
import com.nepninja.locationfinder.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var localDataSource: ReminderDataSource
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()

        localDataSource = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val reminder = ReminderDTO(
            "Birthday Reminder"
            , "Grandma Birthday I need to buy cake"
            , "Big complex freak street"
            , 24.00
            , 48.00000
        )
        localDataSource.saveReminder(reminder)

        val result = localDataSource.getReminder(reminder.id)

        result as Result.Success

        assertThat(result.data.id, Matchers.`is`(reminder.id))
        assertThat(result.data.title, Matchers.`is`(reminder.title))
        assertThat(result.data.description, Matchers.`is`(reminder.description))
        assertThat(result.data.location, Matchers.`is`(reminder.location))
        assertThat(result.data.latitude, Matchers.`is`(reminder.latitude))
        assertThat(result.data.longitude, Matchers.`is`(reminder.longitude))
    }

    @After
    fun cleanUp() {
        database.close()
    }
}