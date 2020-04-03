package com.nepninja.locationfinder.reminderslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.base.BaseViewModel
import com.nepninja.locationfinder.data.ReminderDataSource
import com.nepninja.locationfinder.data.local.RemindersLocalRepository
import com.nepninja.locationfinder.data.local.RemindersLocalRepositoryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*

//@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {
//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    lateinit var mockViewModel: RemindersListViewModel
    private lateinit var repository: RemindersLocalRepositoryTest

    @Before
    fun setUp() {
        mockViewModel = mock(RemindersListViewModel::class.java)
        loadKoinModules(module {
            mockViewModel
        })

        repository = RemindersLocalRepositoryTest()
    }


    @Test
    fun shouldShowError() = runBlocking {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

//        repository.getReminders()

        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(isDisplayed()))
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


    @After
    fun cleanUp() {
        stopKoin()
    }
}