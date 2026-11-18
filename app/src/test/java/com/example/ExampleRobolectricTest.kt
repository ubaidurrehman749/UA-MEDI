package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appNameMatchesHealthMateAi() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HealthMate AI", appName)
    }

    @Test
    fun onboardingAcceptAndNavigateCoreScreens() {
        composeRule.waitForIdle()
        // Onboarding disclaimer is shown on initial launch
        composeRule.onNodeWithTag("onboarding_accept_button").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Home Dashboard is visible
        composeRule.onNodeWithTag("hero_ask_ai_button").assertIsDisplayed()

        // Navigate to Symptom Checker via bottom nav & trigger local emergency red-flag banner
        composeRule.onNodeWithTag("bottom_nav_symptom_checker").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("symptom_main_input").performTextInput("severe chest pain")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("emergency_banner_button").assertIsDisplayed()

        // Navigate to Medication Manager & open add dialog
        composeRule.onNodeWithTag("bottom_nav_medications").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("add_medication_fab").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("med_name_input").performTextInput("Vitamin C")
        composeRule.onNodeWithTag("med_dosage_input").performTextInput("500 mg")
        composeRule.onNodeWithTag("med_save_dialog_btn").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Vitamin C").assertIsDisplayed()
    }
}
