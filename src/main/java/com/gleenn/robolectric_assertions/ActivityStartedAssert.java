package com.gleenn.robolectric_assertions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import org.fest.assertions.api.ANDROID;
import org.fest.assertions.api.Assertions;
import org.robolectric.shadows.ShadowActivity;

public class ActivityStartedAssert {
    public static final int FRAGMENT_REQUESTCODE_OFFSET = (1 << 16);

    private final ShadowActivity.IntentForResult nextStartedActivityForResult;
    private final Intent lastStartedIntent;

    public ActivityStartedAssert(ShadowActivity.IntentForResult nextStartedActivityForResult, Intent lastStartedIntent) {
        this.nextStartedActivityForResult = nextStartedActivityForResult;
        this.lastStartedIntent = lastStartedIntent;
    }

    public ActivityStartedAssert withClass(Class<? extends Activity> startedActivityClass) {
        ComponentName component = lastStartedIntent.getComponent();
        Assertions.assertThat(component).overridingErrorMessage("Expected intent component to not be null").isNotNull();

        String actualClassName = component.getClassName();
        String expectedClassName = startedActivityClass.getCanonicalName();
        Assertions.assertThat(actualClassName)
                .overridingErrorMessage("Expected intent component.getClassName to not be null")
                .isNotNull()
                .overridingErrorMessage("Expected components names to match but didn't, expected: " + expectedClassName + " actual: " + actualClassName)
                .isEqualTo(expectedClassName);

        return this;
    }

    public ActivityStartedAssert withBundleEntry(String key, Object value) {
        Object actualExtraValue = assertExtrasAndReturnValue(key);
        String errorMessage = "Expected intent to have extra with key " + key + " and with value " + value + " but was " + actualExtraValue;
        Assertions.assertThat(actualExtraValue)
                .overridingErrorMessage(errorMessage)
                .isEqualTo(value);

        return this;
    }

    public ActivityStartedAssert forFragmentResult(int expectedCode) {
        int actualCode = nextStartedActivityForResult.requestCode;
        actualCode -= FRAGMENT_REQUESTCODE_OFFSET;
        String errorMessage = "Expected activity to have been started for result with code " + expectedCode + " but was " + actualCode + " assuming google's magic request offset of " + FRAGMENT_REQUESTCODE_OFFSET;
        Assertions.assertThat(actualCode)
                .overridingErrorMessage(errorMessage)
                .isEqualTo(expectedCode);

        return this;
    }

    public ActivityStartedAssert forResult(int expectedCode) {
        int actualCode = nextStartedActivityForResult.requestCode;
        String errorMessage = "Expected activity to have been started for result with code " + expectedCode + " but was " + actualCode;
        Assertions.assertThat(actualCode)
                .overridingErrorMessage(errorMessage)
                .isEqualTo(expectedCode);

        return this;
    }

    public ActivityStartedAssert notForResult() {
        int actualCode = nextStartedActivityForResult.requestCode;
        String errorMessage = "Expected activity to NOT have been started for result";
        Assertions.assertThat(actualCode)
                .overridingErrorMessage(errorMessage)
                .isLessThan(0);

        return this;
    }

    public interface BundleEntryComparer<T> {
        public boolean compareEntry(T actual);
    }
    public <T> ActivityStartedAssert withBundleEntry(String key, BundleEntryComparer<T> bundleEntryComparer) {
        T actualExtraValue = assertExtrasAndReturnValue(key);
        Assertions.assertThat(actualExtraValue)
                .overridingErrorMessage("Expected bundle entry not to be null but was")
                .isNotNull();
        String errorMessage = "Expected intent to have extra with key " + key + " and correct value but was " + actualExtraValue;

        Assertions.assertThat(bundleEntryComparer.compareEntry(actualExtraValue))
                .overridingErrorMessage(errorMessage)
                .isTrue();

        return this;
    }

    private <T> T assertExtrasAndReturnValue(String key) {
        Assertions.assertThat(nextStartedActivityForResult)
                .overridingErrorMessage("Expected an intent to have been sent to this activity but none was found.")
                .isNotNull();

        Intent lastStartedIntent = nextStartedActivityForResult.intent;
        ANDROID.assertThat(lastStartedIntent).overridingErrorMessage("Expected an intent but none was received").isNotNull();

        Bundle extras = lastStartedIntent.getExtras();
        Assertions.assertThat(extras).overridingErrorMessage("Expected extras in last intent but was null").isNotNull();

        return (T)extras.get(key);
    }
}
