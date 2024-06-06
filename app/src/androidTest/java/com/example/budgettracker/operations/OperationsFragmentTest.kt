package com.example.budgettracker.operations

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgettracker.MainActivity
import com.example.budgettracker.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
class OperationsFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testUIElements() {
        Espresso.onView(ViewMatchers.withId(R.id.operationsList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.addOperation))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }
    @Test
    fun testFABInitiallyClosed() {
        // Verify that the FAB is initially closed
        Espresso.onView(ViewMatchers.withId(R.id.overlay))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testFABExpandsAndShrinks() {
        // Open FAB
        Espresso.onView(ViewMatchers.withId(R.id.addOperation)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.overlay))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        // Close FAB
        Espresso.onView(ViewMatchers.withId(R.id.addOperation)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.overlay))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
    
    @Test
    fun testSnackbarAppearsOnSwipe() {
        // Assuming the RecyclerView has some items to swipe
        Espresso.onView(ViewMatchers.withId(R.id.operationsList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.swipeRight()))

        // Verify the Snackbar appears with the expected text
        Espresso.onView(ViewMatchers.withText("Operation deleted"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
