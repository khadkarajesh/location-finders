package com.nepninja.locationfinder.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.nepninja.locationfinder.MainCoroutineRule
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.base.NavigationCommand
import com.nepninja.locationfinder.data.FakeDataSource
import com.nepninja.locationfinder.getOrAwaitValue
import com.nepninja.locationfinder.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var bookPurchaseReminder: ReminderDataItem

    private val context: Application = ApplicationProvider.getApplicationContext()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        bookPurchaseReminder = ReminderDataItem(
            "Buy Book",
            "Buy series of all the boys i loved before",
            "Peoples plaza",
            27.2299,
            48.00029
        )
    }

    @Test
    fun saveReminder_shouldShow_reminderSaved() {
        saveReminderViewModel.saveReminder(bookPurchaseReminder)
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(context.getString(R.string.reminder_saved))
        )
    }

    @Test
    fun saveReminder_shouldNavigateBack() {
        saveReminderViewModel.saveReminder(bookPurchaseReminder)
        assertEquals(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            NavigationCommand.Back
        )
    }

    @Test
    fun saveReminder_loading() {
        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(bookPurchaseReminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun validateEnteredData_shouldReturnTrue() {
        saveReminderViewModel.validateEnteredData(bookPurchaseReminder)
        assertTrue(saveReminderViewModel.validateEnteredData(bookPurchaseReminder))
    }

    @Test
    fun validateEnteredData_onNullTitle_shouldReturnErrorMessage() {
        val reminderDataItem = ReminderDataItem(
            null,
            "Buy series of all the boys i loved before",
            "Peoples plaza",
            27.2299,
            48.00029
        )
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_enter_title)
        )
    }

    @Test
    fun validateEnteredData_onEmptyTitle_shouldReturnErrorMessage() {
        val reminderDataItem = ReminderDataItem(
            "",
            "Buy series of all the boys i loved before",
            "Peoples plaza",
            27.2299,
            48.00029
        )
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_enter_title)
        )
    }

    @Test
    fun validateEnteredData_onEmptyLocation_shouldReturnErrorMessage() {
        val reminderDataItem = ReminderDataItem(
            "Book purchase reminder",
            "Buy series of all the boys i loved before",
            "",
            27.2299,
            48.00029
        )
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_select_location)
        )
    }

    @Test
    fun validateEnteredData_onNullLocation_shouldReturnErrorMessage() {
        val reminderDataItem = ReminderDataItem(
            "Book purchase reminder",
            "Buy series of all the boys i loved before",
            null,
            27.2299,
            48.00029
        )
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(
            context.getString(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()),
            context.getString(R.string.err_select_location)
        )
    }

}