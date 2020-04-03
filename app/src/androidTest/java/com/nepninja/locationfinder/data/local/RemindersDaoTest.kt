package com.nepninja.locationfinder.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.nepninja.locationfinder.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder() = runBlockingTest {
        val reminder = ReminderDTO(
            "Birthday Reminder"
            , "Grandma Birthday I need buy cake"
            , "Big complex freak street"
            , 24.00
            , 48.00000
        )
        database.reminderDao().saveReminder(reminder)

        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(loadedReminder, notNullValue())
        assertThat(reminder.id, `is`(reminder.id))
        assertThat(reminder.title, `is`(reminder.title))
        assertThat(reminder.description, `is`(reminder.description))
        assertThat(reminder.location, `is`(reminder.location))
        assertThat(reminder.latitude, `is`(reminder.latitude))
        assertThat(reminder.longitude, `is`(reminder.longitude))
    }

//    TODO: Add testing implementation to the RemindersDao.kt

}