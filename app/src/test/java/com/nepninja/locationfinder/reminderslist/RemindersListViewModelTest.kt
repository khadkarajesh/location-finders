package com.nepninja.locationfinder.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.nepninja.locationfinder.MainCoroutineRule
import com.nepninja.locationfinder.data.FakeDataSource
import com.nepninja.locationfinder.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var remindersViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        remindersViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun loadRemindersWhenRemindersAreUnavailable_showSnackBar() {
        fakeDataSource.setReturnError(true)
        remindersViewModel.loadReminders()

        assertThat(remindersViewModel.showSnackBar.getOrAwaitValue(), `is`("Reminder not found"))
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()

        remindersViewModel.loadReminders()

        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

}