package com.example.budgettracker.authentication

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgettracker.MainActivity
import com.example.budgettracker.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationFragmentTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun testAuth() {
        // Open FAB to access the Add Expense button
        Espresso.onView(ViewMatchers.withId(R.id.fieldEmail)).perform(ViewActions.typeText("123@mail.com"))
        Espresso.closeSoftKeyboard()

        // Click on the Add Expense button to trigger navigation
        Espresso.onView(ViewMatchers.withId(R.id.fieldPassword)).perform(ViewActions.typeText("123456789"))
        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withId(R.id.emailSignInButton)).perform(ViewActions.click())

    }
}
