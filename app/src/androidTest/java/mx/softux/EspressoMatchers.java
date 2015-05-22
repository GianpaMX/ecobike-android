package mx.softux;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by juan on 5/21/15.
 */
public class EspressoMatchers {
    public static ViewInteraction matchToolbarTitle(final Matcher<String> textMatcher) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(textMatcher)));
    }

    public static Matcher<Object> withToolbarTitle(final Matcher<String> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}
