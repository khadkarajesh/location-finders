package com.nepninja.locationfinder.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.data.ReminderDataSource
import com.nepninja.locationfinder.data.dto.ReminderDTO
import com.nepninja.locationfinder.data.local.FakeRemindersRepository
import com.nepninja.locationfinder.data.local.LocalDB
import com.nepninja.locationfinder.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ReminderListFragmentTest : AutoCloseKoinTest() {
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { FakeRemindersRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Test
    fun showNoData_onEmptyData() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun saveReminder_showData() = runBlockingTest {
        val reminder = ReminderDTO(
            "Birthday Reminder"
            , "Grandma Birthday I need to buy cake"
            , "Big complex freak street"
            , 24.00
            , 48.00000
        )

        repository.saveReminder(reminder)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText("Birthday Reminder")).check(
            matches(
                isDisplayed()
            )
        )
        onView(withText("Grandma Birthday I need to buy cake")).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun getReminders_showError() = runBlockingTest {
        (repository as FakeRemindersRepository).setReturnError(true)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_reminder)))
    }


    @Test
    fun navigateToAddReminder_navigateToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }
}