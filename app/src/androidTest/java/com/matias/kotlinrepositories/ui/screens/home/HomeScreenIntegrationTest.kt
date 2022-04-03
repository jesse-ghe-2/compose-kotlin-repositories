package com.matias.kotlinrepositories.ui.screens.home

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.matias.kotlinrepositories.R
import com.matias.kotlinrepositories.ui.composables.REPO_LIST
import com.matias.kotlinrepositories.util.BaseMockWebserverTest
import com.matias.kotlinrepositories.util.RequestDispatcher
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class HomeScreenIntegrationTest : BaseMockWebserverTest() {

    lateinit var requestDispatcher: RequestDispatcher

    @Before
    override fun setUp() {
        super.setUp()
        requestDispatcher = RequestDispatcher()
        mockWebServer.dispatcher = requestDispatcher
    }

    @Test
    fun givenNoConnection_Then_NetworkErrorIsDisplayed() {
        mockWebServer.shutdown()

        setHomeScreen()
        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.no_internet)), true)
            .assertIsDisplayed()
    }

    @Test
    fun givenUnknownError_Then_UnknownErrorIsDisplayed() {
        requestDispatcher.isEnabled = false
        setHomeScreen()

        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.unknown_error_try_again)), true)
            .assertIsDisplayed()
    }

    @Test
    fun givenNoSearchTerm_Then_CorrectDataIsDisplayed() {
        setHomeScreen()

        val lazyColumn = composeTestRule.onNode(hasTestTag(REPO_LIST))

        lazyColumn.onChildAt(0).assert(hasText("square/okhttp")).assertIsDisplayed()
        lazyColumn.onChildAt(1).assert(hasText("JetBrains/kotlin")).assertIsDisplayed()
        lazyColumn.onChildAt(2).assert(hasText("android/architecture-samples")).assertIsDisplayed()
    }

    @Test
    fun givenNoSearchTerm_When_ScrolledDown_Then_ScrollUpButtonAppears() {
        setHomeScreen()

        composeTestRule.onNode(hasTestTag(REPO_LIST)).performScrollToIndex(10)

        composeTestRule.onNode(hasTestTag(SCROLL_TO_TOP_TEST_TAG)).assertIsDisplayed()
    }

    @Test
    fun givenNoSearchTermAndScrolledDown_When_ClickOnScrollUpButton_Then_FirstItemIsDisplayed() {
        setHomeScreen()

        composeTestRule.onNode(hasTestTag(REPO_LIST)).performScrollToIndex(10)
        composeTestRule.onNode(hasTestTag(SCROLL_TO_TOP_TEST_TAG)).performClick()

        composeTestRule.onNode(hasTestTag(REPO_LIST)).onChildAt(0).assert(hasText("square/okhttp"))
    }

    @Test
    fun givenNoSearchTerm_When_ScrolledToLastElement_Then_NextPageIsLoadedAndFirstElementOfNextPageIsCorrect() {
        setHomeScreen()

        composeTestRule.onNode(hasTestTag(REPO_LIST)).performScrollToIndex(29)
        composeTestRule.onNode(hasTestTag(REPO_LIST)).performScrollToIndex(30)

        composeTestRule.onNode(hasTestTag(REPO_LIST)).onChildAt(0)
            .assert(hasText("YiiGuxing/TranslationPlugin"))
    }

    private fun setHomeScreen() {
        composeTestRule.setContent {
            HomeScreen()
        }
    }
}
