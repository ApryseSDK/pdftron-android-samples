package com.pdftron.android.tutorial.customui;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.pdftron.pdf.PDFViewCtrl;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PageTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void pageTest() {
        // Get oringinal number of apges
        int origNumPages = getPDFViewCtrl().getPageCount();

        // Delete first page in UI
        deletePage();

        // Go back to viewer
        pressBack();
        pressBack();

        // Now check if we have one less page
        int newNumPages = getPDFViewCtrl().getPageCount();
        Assert.assertEquals(newNumPages, origNumPages - 1);
    }

    void deletePage() {

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.controls_thumbnail_slider_left_menu_button), withContentDescription("Browse Thumbnails"),
                        childAtPosition(
                                allOf(withId(R.id.controls_thumbnail_slider_scrubberview),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.controls_action_edit), withText("Edit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.controls_thumbnails_view_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.controls_thumbnails_view_recycler_view),
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1)),
                        0),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.controls_thumbnails_view_action_delete), withContentDescription("Delete"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.controls_thumbnails_view_cab),
                                        1),
                                3),
                        isDisplayed()));
        actionMenuItemView2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }


    protected PDFViewCtrl getPDFViewCtrl() {
        return mActivityTestRule.getActivity().findViewById(R.id.pdfviewctrl);
    }
}
