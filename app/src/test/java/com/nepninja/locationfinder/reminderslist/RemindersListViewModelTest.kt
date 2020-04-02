package com.nepninja.locationfinder.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.nepninja.locationfinder.MainCoroutineRule
import com.nepninja.locationfinder.data.FakeDataSource
import com.nepninja.locationfinder.data.dto.Result
import com.nepninja.locationfinder.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import net.bytebuddy.matcher.ElementMatchers.isEquals
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.Equals
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
    fun loadRemindersShouldReturnReminders_OnSuccess() = runBlockingTest {
        remindersViewModel.loadReminders()
        val data = fakeDataSource.getReminders() as Result.Success
        assertThat(
            remindersViewModel.remindersList.getOrAwaitValue(),
            `is`(notNullValue())
        )
        assertThat(
            remindersViewModel.remindersList.getOrAwaitValue(),
            IsEqual(fakeDataSource.dtoToPojo(data.data))
        )
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