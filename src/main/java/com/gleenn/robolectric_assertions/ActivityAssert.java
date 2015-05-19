package com.gleenn.robolectric_assertions;

import android.app.Activity;
import android.content.Intent;
import org.fest.assertions.api.ANDROID;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.android.app.AbstractActivityAssert;
import org.robolectric.shadows.ShadowActivity;

import static org.robolectric.Robolectric.shadowOf;

public class ActivityAssert extends AbstractActivityAssert<ActivityAssert, Activity> {

    public ActivityAssert(Activity actual) {
        super(actual, ActivityAssert.class);
    }

    public void hasNotHadAnotherActivityStarted() {
        overridingErrorMessage("Expected starting activity not to be null").isNotNull();
        ANDROID.assertThat(actual).overridingErrorMessage("Expected actual activity not to be null").isNotNull();

        final ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(actual).getNextStartedActivityForResult();
        Assertions.assertThat(nextStartedActivityForResult)
                .overridingErrorMessage("Expected NO intent to have been sent to this activity but one was found.")
                .isNull();
    }
    public ActivityStartedAssert hadAnotherActivityStarted() {
        overridingErrorMessage("Expected starting activity not to be null").isNotNull();
        ANDROID.assertThat(actual).overridingErrorMessage("Expected actual activity not to be null").isNotNull();

        final ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(actual).getNextStartedActivityForResult();
        Assertions.assertThat(nextStartedActivityForResult)
                .overridingErrorMessage("Expected an intent to have been sent to this activity but none was found.")
                .isNotNull();

        Intent lastStartedIntent = nextStartedActivityForResult.intent;
        ANDROID.assertThat(lastStartedIntent).overridingErrorMessage("Expected an intent but none was received").isNotNull();

        return new ActivityStartedAssert(nextStartedActivityForResult, lastStartedIntent);
    }

    public interface ResultMatcher<T> {
        public boolean matches(T actual);
    }
    public <T> ActivityAssert finishedWithResult(String key, ResultMatcher<T> resultMatcher) {
        Assertions.assertThat(actual.isFinishing())
                .overridingErrorMessage("Expected activity to be finishing but wasn't")
                .isTrue();
        Object object = shadowOf(actual).getResultIntent().getExtras().get(key);
        Assertions.assertThat(object).overridingErrorMessage("Expected result to not be null").isNotNull();
        Assertions.assertThat(resultMatcher.matches((T) object));
        return this;
    }
}
